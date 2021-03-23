package net.justmachinery.futility.controlflow

import java.time.Duration
import java.time.Instant

/**
 * After every interval that elapses, this will call the periodic callback before the cb passed to next()
 */
public class PeriodicCallbackInterceptor(
    private val interval : Duration,
    callInitially : Boolean = false,
    private val periodically : ()->Unit
){
    private var last = if(callInitially) null else Instant.now()
    public fun <T> next(cb : ()->T) : T {
        val now = Instant.now()
        if(last == null || Duration.between(last, now) >= interval){
            last = now
            periodically()
        }
        return cb()
    }
}