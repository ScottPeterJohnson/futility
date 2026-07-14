package net.justmachinery.futility

/**
 * For the duration of [cb], ThreadLocal will have the specified value, returning to the old value afterwards.
 * On JDK 25+, consider [ScopedValue]. (Though some of its semantics are slightly different.)
 */
public inline fun <T,R> ThreadLocal<T>.withValue(value : T?, cb: ()->R) : R {
    val oldValue = get()
    set(value)
    return try {
        cb()
    } finally {
        set(oldValue)
    }
}