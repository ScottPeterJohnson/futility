package net.justmachinery.futility.threading

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * A simple extension to [ReentrantReadWriteLock] that captures a value accessible within lock-scoped callbacks
 */
public class ValueBehindReadWriteLock<T>( @PublishedApi @Volatile internal var _value : T) {
    @PublishedApi internal val _lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    public fun <R> read(cb : (T)->R) : R {
        return _lock.read { cb(_value) }
    }
    //Inlined for apparent performance reasons
    public inline fun write(cb : (T)->T){
        _lock.write {
            _value = cb(_value)
        }
    }
}
