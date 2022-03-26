package net.justmachinery.futility.threading

import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <T> ReentrantReadWriteLock.read(timeout: Duration, action: () -> T) : T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val rl = readLock()
    if(!rl.tryLock(timeout.toMillis(), TimeUnit.MILLISECONDS)){ throw IllegalStateException("Could not acquire lock $this within $timeout") }
    try {
        return action()
    } finally {
        rl.unlock()
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> ReentrantReadWriteLock.write(timeout: Duration, action: () -> T) : T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    val rl = writeLock()
    if(!rl.tryLock(timeout.toMillis(), TimeUnit.MILLISECONDS)){ throw IllegalStateException("Could not acquire lock $this within $timeout") }
    try {
        return action()
    } finally {
        rl.unlock()
    }
}