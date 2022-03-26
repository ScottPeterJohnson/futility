package net.justmachinery.futility.mechanisms

import net.justmachinery.futility.Maybe

/**
 * A guard that only calls the cb in run() exactly once
 */
public class Once {
    public fun hasRun() : Boolean = synchronized(this) { hasRun }
    private var hasRun: Boolean = false
    public fun <T> maybeRun(cb: () -> T): Maybe<T> {
        val shouldRun = synchronized(this) {
            if (!hasRun) {
                hasRun = true
                true
            } else {
                false
            }
        }
        return if (shouldRun) {
            Maybe.Just(cb())
        } else {
            Maybe.Nothing()
        }
    }
}