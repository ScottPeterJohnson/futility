package net.justmachinery.futility.execution

import kotlinx.coroutines.*
import mu.KLogging
import net.justmachinery.futility.bytes.GiB
import net.justmachinery.futility.bytes.MiB
import org.slf4j.MDC
import java.lang.Runnable
import java.time.Duration
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicLong

private class ExecutionUtility {
    companion object : KLogging()
}

/**
 * Primary pool of executors for background task, expected to be shared and reused.
 * You may replace this pool at runtime (be sure to shut down the old one).
 */
public var defaultExecutor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(
	Runtime.getRuntime().availableProcessors(),
	makeThreadFactory("executor")
).apply {
	//At an guesstimated 1 MB stack size, try to use at most 1/4th of the JVM's memory on threads for this pool.
	maximumPoolSize = (Runtime.getRuntime().maxMemory().coerceAtMost(256L.GiB) / 4 / 1.MiB).toInt()
    executeExistingDelayedTasksAfterShutdownPolicy = false
	addJvmShutdownHook(StopExecutors) {
		ExecutionUtility.logger.info { "Cancelling outstanding coroutines" }
		runBlocking {
			supervisor.cancelAndJoin()
		}
		ExecutionUtility.logger.info { "Shutting down executor service" }
        //I hate this complexity, but we need a way to gracefully shut down an executor whose tasks might submit further tasks.
        outer@ while(true){
            for(i in 0..5){
                if(i != 0){
                    Thread.sleep(10)
                }
                if(activeCount > 0) continue@outer
            }
            break
        }
        this.shutdown()
		this.awaitTermination(99, TimeUnit.DAYS)
		ExecutionUtility.logger.info { "Executor shutdown complete" }
	}
}
private val dispatcher by lazy { defaultExecutor.asCoroutineDispatcher() }
private val supervisor = SupervisorJob()

/**
 * A useful global coroutine scope for executing on the background service
 */
public val backgroundCoroutine: CoroutineScope = CoroutineScope(supervisor + dispatcher)

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
	defaultExecutor.execute(withMdcLogErrors("In background task", cb))
}

/**
 * Executes [cb] after [delay]
 */
public fun scheduled(delay : Duration, cb : ()->Unit) {
	scheduled(delay.toMillis(), TimeUnit.MILLISECONDS, cb)
}

public fun scheduled(delay : Long, timeUnit : TimeUnit, cb : ()->Unit) {
	defaultExecutor.schedule(withMdcLogErrors("In scheduled task", cb), delay, timeUnit)
}



/**
 * Executes [cb] after [initial], then every [delay]
 */
public fun periodically(initial : Long, delay : Long, timeUnit : TimeUnit, cb : ()->Unit) : ScheduledFuture<*> {
    return defaultExecutor.scheduleWithFixedDelay(withMdcLogErrors("In periodic task", cb), initial, delay, timeUnit)
}

/**
 * Runs a [cb] in a background thread, and returns its result as a future.
 * Use of this implies you WILL unwrap the future and any exceptions it generated.
 */
public fun <T> future(cb: () -> T): Future<T> {
	val cmd = withMdc(cb)
	return defaultExecutor.submit(cmd)
}


private val reuseThreads = Executors.newCachedThreadPool(makeThreadFactory("reusable-"))

/**
 * Run command in a reusable background pool of threads. Unlike background {},
 * this guarantees a new thread will be created if none are available.
 */
public fun runThread(command: () -> Unit) : Future<*> {
	return reuseThreads.submit(withMdcLogErrors("While executing runThread") {
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
