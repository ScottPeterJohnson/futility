package net.justmachinery.futility.controlflow


import kotlin.math.min

/**
 * Repeatedly calls [cb] until it returns a non-null value.
 */
public fun <T> repeatUntilNotNull(cb : (Int)->T?) : T {
    var iteration = 0
    while(true){
        val value = cb(iteration)
        if(value != null){
            return value
        }
        iteration += 1
    }
}

/**
 * Calls [cb] up to [maximumAttempts] times, with an exponential thread sleep of up to [maxBackoffWait] ms between attempts.
 * If [cb] returns done(value), return value. If [cb] returns [repeat], then loop again.
 */
public fun <T> conditionalRepeat(
    maximumAttempts : Int? = null,
    initialWait : Long = 100L,
    maxBackoffWait : Long = 0L,
    cb : ConditionalRepeatContext<T>.()->ConditionalRepeat<T>
) : T? {
    val context = ConditionalRepeatContext<T>(maximumAttempts)
    var backoff = min(initialWait, maxBackoffWait)
    while(true){
        when(val result = cb(context)){
            is ConditionalRepeat.Repeat -> {
                context.iteration += 1
                if(maximumAttempts != null && context.iteration >= maximumAttempts){
                    return null
                }
                if(maxBackoffWait != 0L){
                    Thread.sleep(backoff)
                    backoff *= 2
                    backoff = min(backoff, maxBackoffWait)
                }
            }
            is ConditionalRepeat.Done -> {
                return result.value
            }
        }
    }
}
public class ConditionalRepeatContext<T>(private val maximumAttempts : Int?) {
    public var iteration : Int = 0
    public val repeat: ConditionalRepeat.Repeat<T> = ConditionalRepeat.Repeat<T>()
    public fun done(value : T): ConditionalRepeat.Done<T> = ConditionalRepeat.Done(value)
    public val isLastRepeat : Boolean get() = maximumAttempts != null && iteration >= maximumAttempts - 1
}
public val ConditionalRepeatContext<Unit>.done: ConditionalRepeat.Done<Unit> get() = done(Unit)

public sealed class ConditionalRepeat<T> {
    public class Repeat<T> : ConditionalRepeat<T>()
    public data class Done<T>(val value : T) : ConditionalRepeat<T>()
}


/**
 * Repeats [cb] up to [maximumAttempts] times with a maximum thread wait of [maxBackoffWait] between attempts until it
 * does not throw a throwable matching [matchThrowable] during execution.
 * Otherwise, throw the first non-matching throwable or the last matched throwable.
 */
public fun <T> repeatOnThrow(
    maximumAttempts: Int?,
    maxBackoffWait: Long = 0L,
    matchThrowable: (Throwable) -> Boolean = { true },
    cb: () -> T
) : T {
    var lastError : Throwable? = null
    val result = conditionalRepeat<T>(maximumAttempts = maximumAttempts, maxBackoffWait = maxBackoffWait){
        try {
            done(cb())
        } catch(t : Throwable){
            if(matchThrowable(t)){
                lastError = t
                repeat
            } else {
                throw t
            }
        }
    }
    return result ?: throw (lastError ?: IllegalStateException())
}

/**
 * Run [transform] repeatedly until it returns [LoopCollectContext.done], then return all [LoopCollectContext.add] results in a list
 */
public fun <T> loopCollect(transform : LoopCollectContext<T>.()->LoopCollect) : List<T> {
    val results = mutableListOf<T>()
    val context = LoopCollectContext(results)
    while(true){
        if(transform(context) == LoopCollect.DONE){
            return results
        }
        context.first = false
    }
}
public class LoopCollectContext<T>(private val results : MutableList<T>) {
    public val repeat: LoopCollect = LoopCollect.REPEAT
    public val done: LoopCollect = LoopCollect.DONE
    public fun add(value : T) {
        results.add(value)
    }
    public fun addAll(values : List<T>) {
        results.addAll(values)
    }
    public var first : Boolean = true
}
public enum class LoopCollect {
    REPEAT,
    DONE
}

/**
 * Runs [cb] repeatedly until [this] is empty, and ensures that the size of [this] decreases after every call to [cb].
 */
public fun <T> MutableCollection<T>.narrowUntilEmpty(cb : ()->Unit){
    while(this.isNotEmpty()){
        val oldSize = this.size
        cb()
        if(this.size >= oldSize){
            throw IllegalStateException("Collection did not narrow: $this")
        }
    }
}