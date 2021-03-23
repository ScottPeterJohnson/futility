package net.justmachinery.futility

/**
 * Your standard functional Maybe type that can represent either a value or nothing-
 * like Optional with sealed classes.
 * (This should really be in the standard library.)
 */
public sealed class Maybe<T> {
    public class Nothing<T> : Maybe<T>()
    public data class Just<T>(val value : T) : Maybe<T>()

    public fun justOrThrow(t : ()->Throwable) : T {
        if(this is Nothing){ throw t() }
        else { return (this as Just<T>).value }
    }
    public fun justOrNull() : T? = when(this){
        is Nothing -> null
        is Just<T> -> value
    }
}
