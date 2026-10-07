package net.justmachinery.futility.execution

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Demonstrates swapping the global [pools] for a synchronous [TestPools], which makes the otherwise
 * fire-and-forget execution helpers deterministic.
 */
class ExecutionUtilityTest {
    @Test
    fun backgroundSwallowsExceptions() {
        // background {} logs and swallows; it must not propagate even when run inline.
        withPools(TestPools()) {
            background { throw RuntimeException("boom") }
        }
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
