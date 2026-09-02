package net.justmachinery.futility.execution

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Demonstrates swapping the global [pools] for a synchronous [TestPools], which makes the otherwise
 * fire-and-forget execution helpers deterministic.
 */
class ExecutionUtilityTest {
    @Test
    fun futureReturnsResult() {
        withPools(TestPools()) {
            assertEquals(42, future { 21 * 2 }.get())
        }
    }

    @Test
    fun futurePropagatesExceptionThroughGet() {
        withPools(TestPools()) {
            val f = future { throw IllegalStateException("nope") }
            val thrown = assertFailsWith<ExecutionException> { f.get() }
            assertTrue(thrown.cause is IllegalStateException)
        }
    }

    @Test
    fun backgroundRunsSynchronouslyUnderTestPools() {
        var ran = false
        withPools(TestPools()) {
            background { ran = true }
        }
        assertTrue(ran)
    }

    @Test
    fun backgroundSwallowsExceptions() {
        // background {} logs and swallows; it must not propagate even when run inline.
        withPools(TestPools()) {
            background { throw RuntimeException("boom") }
        }
    }

    @Test
    fun runThreadExecutesAndCompletes() {
        var ran = false
        withPools(TestPools()) {
            runThread { ran = true }.get()
        }
        assertTrue(ran)
    }

    @Test
    fun scheduledEventuallyRunsCallback() {
        val latch = CountDownLatch(1)
        withPools(TestPools()) {
            scheduled(Duration.ofMillis(1)) { latch.countDown() }
            assertTrue(latch.await(5, TimeUnit.SECONDS))
        }
    }
}
