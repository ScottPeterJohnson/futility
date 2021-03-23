package net.justmachinery.futility.logging

import mu.KLogger
import org.slf4j.MDC
import org.slf4j.event.Level

public inline fun <T> withLoggingInfo(vararg pair: Pair<String, String>, body: () -> T): T {
    val oldValues = arrayOfNulls<String?>(pair.size)
    try {
        pair.forEachIndexed { index, it ->
            oldValues[index] = MDC.get(it.first)
            MDC.put(it.first, it.second)
        }
        return body()
    } finally {
        pair.forEachIndexed { index, it ->
            if(oldValues[index] == null){
                MDC.remove(it.first)
            } else {
                MDC.put(it.first, oldValues[index])
            }
        }
    }
}

public fun KLogger.logAt(level : Level, message : String, throwable: Throwable?){
    if(throwable != null){
        when(level){
            Level.ERROR -> error(message, throwable)
            Level.WARN -> warn(message, throwable)
            Level.INFO -> info(message, throwable)
            Level.DEBUG -> debug(message, throwable)
            Level.TRACE -> trace(message, throwable)
        }
    } else {
        when(level){
            Level.ERROR -> error(message)
            Level.WARN -> warn(message)
            Level.INFO -> info(message)
            Level.DEBUG -> debug(message)
            Level.TRACE -> trace(message)
        }
    }
}