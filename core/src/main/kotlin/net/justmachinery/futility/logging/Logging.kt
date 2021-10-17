package net.justmachinery.futility.logging

import mu.KLogger
import org.slf4j.MDC
import org.slf4j.event.Level


public interface MdcLoggable {
    public val mdc : Sequence<MdcPair>
}
public class MdcPair(public val key : String, public val value : String) : MdcLoggable {
    public override val mdc: Sequence<MdcPair> get() = sequenceOf(this)
}

public fun <T : MdcLoggable> Iterable<T>.mdcCombine() : MdcLoggable = CombinedMdcLoggable(this)
private data class CombinedMdcLoggable(val values : Iterable<MdcLoggable>) : MdcLoggable {
    override val mdc : Sequence<MdcPair>
    init {
        val values = values.asSequence().map { it.mdc }.flatten()
        mdc = values.groupingBy { it.key }.aggregate<MdcPair, String, StringBuilder> { _, builder, pairs, _ ->
            (builder?.append(",") ?: StringBuilder()).append(pairs.value)
        }.asSequence().map { MdcPair(it.key, it.value.toString()) }
    }
}

public inline fun <T> withLoggingInfo(values : Sequence<MdcLoggable>, body: () -> T): T {
    val oldKeys = mutableListOf<String>()
    val oldValues = mutableListOf<String?>()
    try {
        values.forEach {
            it.mdc.forEach { mdc ->
                oldKeys.add(mdc.key)
                oldValues.add(MDC.get(mdc.key))
                MDC.put(mdc.key, mdc.value)
            }
        }
        return body()
    } finally {
        for(index in oldKeys.indices.reversed()){
            val value = oldValues[index]
            if(oldValues[index] == null){
                MDC.remove(oldKeys[index])
            } else {
                MDC.put(oldKeys[index], oldValues[index])
            }
        }
    }
}
public inline fun <T> withLoggingInfo(values : Iterable<MdcLoggable>, body: () -> T): T = withLoggingInfo(values.asSequence(), body)

public inline fun <T> withLoggingInfo(vararg pair: MdcLoggable, body: () -> T): T = withLoggingInfo(pair.asSequence(), body)

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