package net.justmachinery.futility.mechanisms

import mu.KLogging
import net.justmachinery.futility.controlflow.Wrapper
import net.justmachinery.futility.execution.background
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A class that controls an asynchronous task of which only one instance can run at a time.
 * If [run] is called during execution, another execution will take place afterwards.
 * [executionMethod] by default runs synchronously in a calling thread , but could be ::[background] or a coroutine invoker
 */
public class SingleConcurrentExecution(
    private val cb : ()->Unit,
    private val executionMethod : Wrapper<Unit> = { it() }
) {
    private companion object : KLogging()

    private var running = AtomicBoolean(false)
    private var required = AtomicBoolean(false)

    public fun run(){
        val toRun = synchronized(this){
            required.set(true)
            if(!running.get()){
                running.set(true)
                true
            } else {
                false
            }
        }
        if(toRun){
            executionMethod(::runInternal)
        }
    }

    private fun runInternal(){
        while(true){
            logger.trace { "Starting job" }
            synchronized(this){
                required.set(false)
            }
            try {
                cb()
            } catch(t : Throwable){
                logger.error(t){ "While running single concurrent job" }
            }
            val shouldRunAgain = synchronized(this){
                if(required.get()){
                    true
                } else {
                    running.set(false)
                    false
                }
            }
            if(shouldRunAgain){
                continue
            } else {
                break
            }
        }
    }
}