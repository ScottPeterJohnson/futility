package net.justmachinery.futility.mechanisms

import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

/**
 * Simply holds both a lock and an associated condition.
 */
public class LockAndCondition<LockType : Lock>(
    public val lock : LockType
) {
    public val condition : Condition = lock.newCondition()

    public inline fun <T> withLock(cb : ()->T) : T = lock.withLock(cb)

    public fun lockAndAwait(){
        lock.withLock { condition.await() }
    }
    public fun lockAndAwaitUninterruptably(){
        lock.withLock { condition.awaitUninterruptibly() }
    }
    public fun lockAndAwaitNanos(nanosTimeout : Long){
        lock.withLock { condition.awaitNanos(nanosTimeout) }
    }
    public fun lockAndAwait(time : Long, unit : TimeUnit){
        lock.withLock { condition.await(time, unit) }
    }
    public fun lockAndAwaitUntil(deadline : Date){
        lock.withLock { condition.awaitUntil(deadline) }
    }
    public fun lockAndSignal(){
        lock.withLock { condition.signal() }
    }
    public fun lockAndSignalAll(){
        lock.withLock { condition.signalAll() }
    }
}

public fun <LockType : Lock> LockType.withCondition() : LockAndCondition<LockType> = LockAndCondition(this)