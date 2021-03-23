package net.justmachinery.futility.strings

/**
 * CharSequence that notices thread interrupts on access.
 * Useful for early termination of long-running regexes.
 */
public class InterruptibleCharSequence(private var inner: CharSequence) : CharSequence {
    override val length: Int get() = inner.length
    override fun get(index: Int): Char {
        if (Thread.interrupted()) {
            throw InterruptedException()
        }
        return inner[index]
    }
    override fun subSequence(startIndex: Int, endIndex: Int): InterruptibleCharSequence = InterruptibleCharSequence(inner.subSequence(startIndex, endIndex))
    override fun toString(): String = inner.toString()
}