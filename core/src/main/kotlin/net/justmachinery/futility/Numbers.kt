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

public fun Int.atMost(other : Int) : Int = coerceAtMost(other)
public fun Long.atMost(other: Long): Long = coerceAtMost(other)
public fun Double.atMost(other: Double): Double = coerceAtMost(other)
public fun Float.atMost(other: Float): Float = coerceAtMost(other)

public fun Int.atLeast(other : Int) : Int = coerceAtLeast(other)
public fun Long.atLeast(other: Long): Long = coerceAtLeast(other)
public fun Double.atLeast(other: Double): Double = coerceAtLeast(other)
public fun Float.atLeast(other: Float): Float = coerceAtLeast(other)

public fun Int.clamp(minimumValue : Int, maximumValue : Int) : Int = coerceIn(minimumValue, maximumValue)
public fun Long.clamp(minimumValue: Long, maximumValue: Long): Long = coerceIn(minimumValue, maximumValue)
public fun Double.clamp(minimumValue: Double, maximumValue: Double): Double = coerceIn(minimumValue, maximumValue)
public fun Float.clamp(minimumValue: Float, maximumValue: Float): Float = coerceIn(minimumValue, maximumValue)

/**
 * Whether [this] is within [tolerance] of [other], inclusive.
 */
public fun Int.isWithin(other : Int, tolerance : Int) : Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Long.isWithin(other: Long, tolerance: Long): Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Double.isWithin(other: Double, tolerance: Double): Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Float.isWithin(other: Float, tolerance: Float): Boolean = this in ((other - tolerance)..(other + tolerance))
