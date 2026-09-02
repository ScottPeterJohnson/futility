package net.justmachinery.futility

import kotlin.math.sqrt

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
public fun Long.divRoundUp(divisor : Long) : Long = Math.ceilDiv(this, divisor)
public fun Int.divRoundUp(divisor : Int) : Int = Math.ceilDiv(this, divisor)


public fun Int.squared() : Int = this * this
public fun Long.squared() : Long = this * this
public fun Double.squared(): Double = this * this
public fun Float.squared(): Float = this * this


public fun Double.sqrt() : Double = sqrt(this)
public fun Float.sqrt() : Float = sqrt(this.toDouble()).toFloat()

public fun Int.min(other : Int) : Int = coerceAtMost(other)
public fun Long.min(other: Long): Long = coerceAtMost(other)
public fun Double.min(other: Double): Double = coerceAtMost(other)
public fun Float.min(other: Float): Float = coerceAtMost(other)

public fun Int.max(other : Int) : Int = coerceAtLeast(other)
public fun Long.max(other: Long): Long = coerceAtLeast(other)
public fun Double.max(other: Double): Double = coerceAtLeast(other)
public fun Float.max(other: Float): Float = coerceAtLeast(other)

/**
 * Whether [this] is within [tolerance] of [other], inclusive.
 */
public fun Int.isWithin(other : Int, tolerance : Int) : Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Long.isWithin(other: Long, tolerance: Long): Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Double.isWithin(other: Double, tolerance: Double): Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Float.isWithin(other: Float, tolerance: Float): Boolean = this in ((other - tolerance)..(other + tolerance))
