package net.justmachinery.futility

import mu.KLogging
import java.lang.ref.Cleaner

/**
 * A global and reusable instance of [Cleaner]
 */
public var globalCleaner : Cleaner = Cleaner.create()!!

/**
 * If close() is not called before the [CleanerWatcher] is garbage collected, it will log a stack error
 * and call close()
 */
public class CleanerWatcher<T : AutoCloseable>(_value : T, createStack : Boolean = true) : AutoCloseable {
    private companion object : KLogging()

    public val value: T get() = inner.value
    private val inner = Inner(_value, if(createStack) CleanerWatcherException() else null)
    private val cleaner = globalCleaner.register(this, inner)
    private class Inner<T : AutoCloseable>(val value : T, val stack : CleanerWatcherException?) : Runnable {
        @Volatile var wasClosed = false
        override fun run() {
            if(!wasClosed){
                logger.error(stack) { "Value not properly closed: $value" }
            }
            value.close()
        }
    }
    public override fun close(){
        inner.wasClosed = true
        cleaner.clean()
    }
}

public class CleanerWatcherException : RuntimeException()