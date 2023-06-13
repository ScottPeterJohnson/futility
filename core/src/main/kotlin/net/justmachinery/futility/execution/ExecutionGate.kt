package net.justmachinery.futility.execution

import java.lang.IllegalStateException

/**
 * A queue that can be flushed exactly once with a value; it will call in order all callbacks waiting on it.
 * If it has already been completed, it will immediately call a callback that tries to wait on it.
 */
public class ExecutionGate<T> {
    private var waiting : MutableList<(T)->Unit>? = mutableListOf()
    private var haveValue : Boolean = false
    private var value : T? = null


    public fun whenOpen(cb : (T)->Unit){
        val callNow = synchronized(this){
            if(haveValue){
                true
            } else {
                waiting?.add(cb)
                false
            }
        }
        if(callNow){
            @Suppress("UNCHECKED_CAST")
            cb(value as T)
        }
    }

    public fun open(value : T){
        synchronized(this){
            if(haveValue){
                throw IllegalStateException("FutureExecutionQueue was already completed with $value")
            } else {
                haveValue = true
                this.value = value
            }
        }
        try {
            waiting?.forEach { cb ->
                cb(value)
            }
        } finally {
            waiting = null
        }
    }
}