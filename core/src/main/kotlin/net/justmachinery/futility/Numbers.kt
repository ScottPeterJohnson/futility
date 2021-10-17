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

/**
 * Divides [this] by [divisor], rounding up if there is any remainder.
 */
public fun Long.divRoundUp(divisor : Long) : Long = (this + divisor - 1) / divisor
public fun Int.divRoundUp(divisor : Int) : Int = (this + divisor - 1) / divisor