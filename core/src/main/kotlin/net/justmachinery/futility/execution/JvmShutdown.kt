/**
 *  This package provides useful utilities for ordering JVM shutdown hooks
 */
package net.justmachinery.futility.execution

import mu.KLogging
import net.justmachinery.futility.swallowExceptions
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean


public data class ShutdownHookPriority(val priority : Double) : Comparable<ShutdownHookPriority> {
    public companion object {
        public val EXECUTOR_STOP_TASKS: ShutdownHookPriority = ShutdownHookPriority(10.0)
    }
    override fun compareTo(other: ShutdownHookPriority): Int = this.priority.compareTo(other.priority)
}
public val StopExecutors: ShutdownHookPriority = ShutdownHookPriority(1.0)

public fun addJvmShutdownHook(priority : ShutdownHookPriority, cb: ()->Unit){
    val hasRun = AtomicBoolean(false)
    val hook = ShutdownHook(hasRun = hasRun, priority = priority, cb = {
        if (!hasRun.getAndSet(true)) {
            cb()
        }
    })
    shutdownHooks.add(hook)
}

public fun runJvmShutdownHooksPrematurely(){
    Shutdown.logger.info { "Prematurely calling JVM shutdown hooks" }
    runShutdownHooks()
}

private fun runShutdownHooks(){
    while(true){
        val hook = shutdownHooks.poll() ?: break
        swallowExceptions(message = "While shutting down"){
            hook.cb()
        }
    }
}


private data class ShutdownHook(var hasRun : AtomicBoolean, val cb : ()->Unit, val priority : ShutdownHookPriority)

private val shutdownHooks = PriorityBlockingQueue<ShutdownHook>(1) { left, right -> left.priority.compareTo(right.priority) }.apply {
    val logger = Shutdown.logger //This is here to escape unusual shutdown classloading issues
    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Calling JVM Shutdown hooks")
        runShutdownHooks()
    })
}

internal object Shutdown : KLogging()