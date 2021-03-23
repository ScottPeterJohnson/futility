package net.justmachinery.futility

import mu.KLogger
import mu.KLogging

/**
 * Return null if [cb] throws any exception
 */
public fun <R> nullOnException(cb : ()->R) : R? =
    try { cb() }
    catch(t : Throwable){
        null
    }

/**
 * Convert the type of any exceptions thrown within [cb] using [ex]
 */
public inline fun <R> convertThrows(ex : (Throwable)->Throwable, cb : ()->R) : R {
    try {
        return cb()
    } catch(t : Throwable){
        throw ex(t)
    }
}

public object SwallowedExceptions : KLogging()

public inline fun swallowExceptions(message : String = "Swallowed", logger : KLogger = SwallowedExceptions.logger, cb : ()->Unit) {
    swallowExceptions<Unit>(message, logger, cb)
}
public inline fun <T> swallowExceptions(message : String = "Swallowed", logger : KLogger = SwallowedExceptions.logger, cb : ()->T) : T? {
    return swallowExceptions({ message }, logger, cb)
}

/**
 * Run [cb], catching and logging any exceptions generated
 */
public inline fun <T> swallowExceptions(crossinline message : ()->String, logger : KLogger = SwallowedExceptions.logger, cb : ()->T) : T? {
    return try {
        cb()
    } catch(e : Throwable){
        logger.error(e) { message() }
        null
    }
}

/**
 * Run each [cb] to completion and rethrow if any threw an exception
 */
public fun exceptionIndependent(vararg cbs : ()->Unit){
    val exceptions = mutableListOf<Throwable>()
    cbs.forEach {
        try { it() }
        catch(t : Throwable){ exceptions.add(t) }
    }
    if(exceptions.isNotEmpty()){
        throw exceptions.foldAsSuppressed()
    }
}

/**
 * Turn many throwables into one
 */
public fun List<Throwable>.foldAsSuppressed() : Throwable {
    this.drop(1).forEach {
        this.first().addSuppressed(it)
    }
    return this.first()
}
