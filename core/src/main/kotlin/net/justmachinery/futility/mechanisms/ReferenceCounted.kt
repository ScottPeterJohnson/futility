package net.justmachinery.futility.mechanisms

import net.justmachinery.futility.logging.KLogging
import net.justmachinery.futility.Maybe

public interface ReferenceCounted<T> {
    public fun tryAcquireUse() : Boolean
    public fun requireUse() {
        if(!tryAcquireUse()){
            throw IllegalStateException("Could not acquire a usage")
        }
    }
    public fun releaseUse()
    public val value : T
    public fun <R> tryUse(cb : (T)-> R) : Maybe<R> {
        if(!tryAcquireUse()){ return Maybe.Nothing() }
        return try {
            Maybe.Just(cb(value))
        } finally {
            releaseUse()
        }
    }
    public fun <R> requireUse(cb : (T)-> R) : R {
        requireUse()
        return try {
            cb(value)
        } finally {
            releaseUse()
        }
    }
}

/**
 * Holds an initial use on behalf of its creator; [close] releases it.
 * [value] is closed when the last use is released.
 */
public class CloseableReferenceCounter<T : AutoCloseable>(override val value : T) : ReferenceCounted<T>, AutoCloseable {
    public companion object : KLogging()
    private var uses = 1

    override fun tryAcquireUse(): Boolean {
        return synchronized(this){
            if(uses <= 0){
                false
            } else {
                uses += 1
                true
            }
        }
    }

    override fun releaseUse(){
        val remaining = synchronized(this){
            if(uses <= 0){
                throw IllegalStateException("Could not release use from reference counter")
            }
            uses -= 1
            uses
        }
        if(remaining == 0){
            value.close()
        }
    }

    override fun close(): Unit = releaseUse()
}