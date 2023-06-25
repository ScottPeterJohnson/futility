package net.justmachinery.futility.mechanisms

import mu.KLogging
import net.justmachinery.futility.execution.periodically
import java.lang.ref.WeakReference
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * A lock that needs to "refresh" itself before a failsafe release period elapses.
 * This could be e.g. a row in the DB which is assumed unlocked after [failsafeReleaseAfter] elapses without refresh.
 * @param refreshCb Refresh whatever backs this lock; if [until] is null, then it should clear the lock.
 */
public class RefreshingLock(
    private val refreshCb : (until : Instant?)->Unit,
    private val failsafeReleaseAfter : Duration = DEFAULT_FAILSAFE_RELEASE_AFTER
){
    private companion object : KLogging() {
        val DEFAULT_FAILSAFE_RELEASE_AFTER = Duration.ofMinutes(5)!!
    }


    private var wasClosed = false

    private val lock : ScheduledFuture<*>

    init {
        val refreshAfter = failsafeReleaseAfter.dividedBy(2)!!
        val refresh = RefreshingLockRefresh(
            lock = WeakReference(this),
        )
        lock = periodically(
            initial = refreshAfter.toMinutes(),
            delay = refreshAfter.toMinutes(),
            timeUnit = TimeUnit.MINUTES,
            cb = refresh::doRefresh
        )
    }

    private class RefreshingLockRefresh(
        private val lock : WeakReference<RefreshingLock>
    ){
        fun doRefresh(){
            val refreshingLock = lock.get()
            if(refreshingLock == null){
                logger.warn { "RefreshingLock was not closed properly" }
            } else {
                synchronized(refreshingLock){
                    if(!refreshingLock.wasClosed){
                        refreshingLock.refreshCb(Instant.now().plus(refreshingLock.failsafeReleaseAfter))
                    }
                }
            }
        }
    }

    /**
     * Cancel refresh, release lock.
     */
    public fun release(){
        cancelRefresh()
        synchronized(this){
            refreshCb(null)
        }
    }

    /**
     * Cancel refresh, keep lock (until it expires).
     */
    public fun cancelRefresh(){
        wasClosed = true
        lock.cancel(false)
    }
}