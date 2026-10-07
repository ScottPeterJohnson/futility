package net.justmachinery.futility.mechanisms

import java.time.Duration
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertTrue

class RefreshingLockTest {
    //Sub-minute failsafes used to truncate to a zero scheduling period and throw on construction
    @Test
    fun refreshesSubMinuteFailsafe(){
        val refreshed = CountDownLatch(1)
        var releasedUntil : Instant? = Instant.MAX
        val lock = RefreshingLock(
            refreshCb = { until ->
                if(until != null){ refreshed.countDown() }
                else { releasedUntil = null }
            },
            failsafeReleaseAfter = Duration.ofMillis(300)
        )
        assertTrue(refreshed.await(5, TimeUnit.SECONDS), "Refresh callback should have fired")
        lock.release()
        assertTrue(releasedUntil == null, "Release should clear the lock")
    }
}
