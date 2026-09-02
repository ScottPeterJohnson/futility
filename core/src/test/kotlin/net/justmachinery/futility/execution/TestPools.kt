package net.justmachinery.futility.execution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * An [ExecutorService] that runs every submitted task immediately on the calling thread.
 * Combined with [TestPools] this makes [background]/[future]/[runThread] fully synchronous and deterministic in tests.
 */
class DirectExecutorService : AbstractExecutorService() {
    @Volatile private var stopped = false
    override fun execute(command: Runnable) { command.run() }
    override fun shutdown() { stopped = true }
    override fun shutdownNow(): MutableList<Runnable> { stopped = true; return mutableListOf() }
    override fun isShutdown(): Boolean = stopped
    override fun isTerminated(): Boolean = stopped
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = true
}

/**
 * An [ExecutionPools] whose task executors run synchronously in the calling thread.
 * The scheduler remains a real single-thread scheduler so delayed tasks still fire.
 */
class TestPools(
    override val defaultExecutor: ExecutorService = DirectExecutorService(),
    override val reuseThreads: ExecutorService = DirectExecutorService(),
    override val schedulerService: ScheduledExecutorService = ScheduledThreadPoolExecutor(1),
    override val coroutines: CoroutineScope = CoroutineScope(Dispatchers.Unconfined),
) : ExecutionPools

/**
 * Installs [replacement] as the global [pools] for the duration of [block], then restores the previous value.
 * Tests that mutate global pools must not run concurrently; JUnit runs within a class sequentially by default.
 */
fun <T> withPools(replacement: ExecutionPools, block: () -> T): T {
    val previous = pools
    pools = replacement
    try {
        return block()
    } finally {
        pools = previous
    }
}
