package net.justmachinery.futility

/**
 * Returns [this], unless [this] is outside of [Integer.MIN_VALUE] to [Integer.MAX_VALUE]; in which case returns the closest max.
 */
public fun Long.clampToInt() : Int {
    return when {
        this > Integer.MAX_VALUE -> Integer.MAX_VALUE
        this < Integer.MIN_VALUE -> Integer.MIN_VALUE
        else -> this.toInt()
    }
}