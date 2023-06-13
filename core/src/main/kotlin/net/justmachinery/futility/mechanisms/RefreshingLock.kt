package net.justmachinery.futility.mechanisms

import mu.KLogging
import net.justmachinery.futility.execution.periodically
import net.justmachinery.futility.globalCleaner
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * A lock that needs to "refresh" itself before a failsafe release period elapses.
 * This could be e.g. a row in the DB which is assumed unlocked after [failsafeReleaseAfter] elapses without refresh.
 * @param refreshCb Refresh whatever backs this lock; if [until] is null, then it should clear the lock.
 */
public class RefreshingLock(
    refreshCb : (until : Instant?)->Unit,
    failsafeReleaseAfter : Duration = DEFAULT_FAILSAFE_RELEASE_AFTER
){
    private companion object : KLogging() {
        val DEFAULT_FAILSAFE_RELEASE_AFTER = Duration.ofMinutes(5)!!
    }
    private val implementation = Implementation(refreshCb, failsafeReleaseAfter)
    init {
        val impl = implementation
        globalCleaner.register(this){
            if(!impl.wasClosed){
                logger.error { "Refreshing lock was not properly closed" }
                impl.release()
            }
        }
    }

    //Do not capture the outer lock; allow garbage collection
    private class Implementation(private val refreshCb : (until : Instant?)->Unit,
                                 private val failsafeReleaseAfter : Duration = DEFAULT_FAILSAFE_RELEASE_AFTER
    ){
        var wasClosed = false
        private val refreshAfter = failsafeReleaseAfter.dividedBy(2)!!

        private val lock = periodically(refreshAfter.toMinutes(), refreshAfter.toMinutes(), TimeUnit.MINUTES){
            synchronized(this){
                if(!wasClosed){
                    refreshCb(Instant.now().plus(failsafeReleaseAfter))
                }
            }
        }

        fun release(){
            cancelRefresh()
            synchronized(this){
                refreshCb(null)
            }
        }


        fun cancelRefresh(){
            wasClosed = true
            lock.cancel(false)
        }
    }

    /**
     * Cancel refresh, release lock.
     */
    public fun release(){
        implementation.release()
    }

    /**
     * Cancel refresh, keep lock (until it expires).
     */
    public fun cancelRefresh(){
        implementation.cancelRefresh()
    }
}