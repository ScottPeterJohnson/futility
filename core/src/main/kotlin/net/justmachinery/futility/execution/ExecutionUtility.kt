package net.justmachinery.futility.execution

import kotlinx.coroutines.*
import mu.KLogging
import net.justmachinery.futility.bytes.GiB
import net.justmachinery.futility.bytes.MiB
import net.justmachinery.futility.lazyMutable
import org.slf4j.MDC
import java.lang.Runnable
import java.time.Duration
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicLong


public var pools : ExecutionPools by lazyMutable { DefaultPools() }

public interface ExecutionPools {
	/**
	 * Single-thread scheduler service
	 */
	public val schedulerService : ScheduledExecutorService
	/**
	 * Primary pool of executors for background tasks, expected to be shared and reused.
	 */
	public val defaultExecutor : ExecutorService

	/**
	 * A global scope for launching coroutines- perhaps based on the executor service
	 */
	public val coroutines : CoroutineScope

	/**
	 * An executor service which should always have new threads available, and can reuse old ones
	 */
	public val reuseThreads : ExecutorService
}

public class DefaultPools : ExecutionPools {
	public override var schedulerService: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1, makeThreadFactory("scheduler")).apply {
		executeExistingDelayedTasksAfterShutdownPolicy = false
	}

	@Volatile private var shuttingDown = false
	public override var defaultExecutor: ThreadPoolExecutor = run {
		//See https://stackoverflow.com/a/24493856
		val queue = object : LinkedTransferQueue<Runnable>() {
			override fun offer(e: Runnable): Boolean {
				return tryTransfer(e)
			}
		}
		val maxPoolSize = (Runtime.getRuntime().maxMemory().coerceAtMost(256L.GiB) / 4 / 1.MiB).toInt()
		val pool = ThreadPoolExecutor(10, maxPoolSize, 15, TimeUnit.SECONDS, queue)

		pool.threadFactory = makeThreadFactory("executor")
		pool.rejectedExecutionHandler = RejectedExecutionHandler { r, executor ->
			try {
				if(!shuttingDown){
					executor.queue.put(r)
				} else {
					throw RejectedExecutionException("Shutting down")
				}
			} catch (e: InterruptedException) {
				Thread.currentThread().interrupt()
			}
		}

		addJvmShutdownHook(ShutdownHookPriority.EXECUTOR_STOP_TASKS) {
			ExecutionUtility.logger.info { "Cancelling outstanding coroutines" }
			runBlocking {
				supervisor.cancelAndJoin()
			}
			ExecutionUtility.logger.info { "Shutting down executor service" }
			//I hate this complexity, but we need a way to gracefully shut down an executor whose tasks might submit further tasks.
			while(pool.activeCount > 0){
				Thread.sleep(10)
			}
			shuttingDown = true
			schedulerService.shutdown()
			pool.shutdown()
			pool.awaitTermination(99, TimeUnit.DAYS)
			ExecutionUtility.logger.info { "Executor shutdown complete" }
		}
		pool
	}
	private val dispatcher: CoroutineDispatcher = defaultExecutor.asCoroutineDispatcher()
	private val supervisor: Job = SupervisorJob()
	public override val coroutines: CoroutineScope = CoroutineScope(supervisor + dispatcher)

	public override val reuseThreads: ExecutorService = Executors.newCachedThreadPool(makeThreadFactory("reusable-"))
}

private fun makeThreadFactory(namePrefix : String) : ThreadFactory {
	return object : ThreadFactory {
		val backing = Executors.defaultThreadFactory()
		val count = AtomicLong(0)
		override fun newThread(r: Runnable): Thread {
			val thread = backing.newThread(r)
			thread.name = "$namePrefix${count.getAndIncrement()}"
			thread.isDaemon = true
			thread.priority = Thread.NORM_PRIORITY
			thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, exception ->
				ExecutionUtility.logger.error(exception) { "Error in executor $namePrefix task" }
			}
			return thread
		}
	}
}

/**
 * Executes [cb] in a background thread.
 * This and all other scheduled functions preserve the MDC logging context in the new thread.
 */
public fun background(cb: () -> Unit) {
	pools.defaultExecutor.execute(withMdcLogErrors("In background task", cb))
}

/**
 * Executes [cb] after [delay]
 */
public fun scheduled(delay : Duration, cb : ()->Unit) {
	scheduled(delay.toMillis(), TimeUnit.MILLISECONDS, cb)
}

public fun scheduled(delay : Long, timeUnit : TimeUnit, cb : ()->Unit) {
	val finalCb = withMdcLogErrors("In scheduled task", cb)
	pools.schedulerService.schedule({ pools.defaultExecutor.execute(finalCb) }, delay, timeUnit)
}



/**
 * Executes [cb] after [initial], then every [delay]
 */
public fun periodically(initial : Long, delay : Long, timeUnit : TimeUnit, cb : ()->Unit) : ScheduledFuture<*> {
	val finalCb = withMdcLogErrors("In periodically scheduled task", cb)
	return pools.schedulerService.scheduleWithFixedDelay({ pools.defaultExecutor.execute(finalCb) }, initial, delay, timeUnit)
}

/**
 * Runs a [cb] in a background thread, and returns its result as a future.
 * Use of this implies you WILL unwrap the future and any exceptions it generated.
 */
public fun <T> future(cb: () -> T): Future<T> {
	val cmd = withMdc(cb)
	return pools.defaultExecutor.submit(cmd)
}




/**
 * Run command in a reusable background pool of threads. Unlike background {},
 * this guarantees a new thread will be created if none are available.
 */
public fun runThread(command: () -> Unit) : Future<*> {
	return pools.reuseThreads.submit(withMdcLogErrors("While executing runThread") {
		command()
	})
}



private fun withMdcLogErrors(info : String = "Toplevel", cb : ()->Unit) : ()->Unit {
	val copy = MDC.getCopyOfContextMap()
	return {
		try {
			copy?.let { MDC.setContextMap(it) }
			cb()
		} catch(t : Throwable){
			if(t is CancellationException){
				ExecutionUtility.logger.info { "$info cancelled" }
			} else {
				ExecutionUtility.logger.error(t){ info }
			}
		} finally {
			MDC.clear()
		}
	}
}

private fun <T> withMdc(cb : ()->T) : ()->T {
	val copy = MDC.getCopyOfContextMap()
	return {
		try {
			copy?.let { MDC.setContextMap(it) }
			cb()
		} finally {
			MDC.clear()
		}
	}
}

private class ExecutionUtility {
	companion object : KLogging()
}