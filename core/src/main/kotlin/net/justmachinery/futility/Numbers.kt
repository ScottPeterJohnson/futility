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
 * Divides [this] by [divisor], rounding towards positive infinity. See [Math.ceilDiv].
 */
public fun Long.ceilDiv(divisor : Long) : Long = Math.ceilDiv(this, divisor)
public fun Int.ceilDiv(divisor : Int) : Int = Math.ceilDiv(this, divisor)

/**
 * The remainder of [ceilDiv]: zero or the opposite sign of [divisor]. See [Math.ceilMod].
 */
public fun Long.ceilMod(divisor : Long) : Long = Math.ceilMod(this, divisor)
public fun Int.ceilMod(divisor : Int) : Int = Math.ceilMod(this, divisor)

@Deprecated("Use ceilDiv", ReplaceWith("this.ceilDiv(divisor)"))
public fun Long.divRoundUp(divisor : Long) : Long = ceilDiv(divisor)
@Deprecated("Use ceilDiv", ReplaceWith("this.ceilDiv(divisor)"))
public fun Int.divRoundUp(divisor : Int) : Int = ceilDiv(divisor)

/*
 * Arithmetic that throws [ArithmeticException] on overflow instead of wrapping. See [Math.addExact] and friends.
 */
public infix fun Int.plusExact(other : Int) : Int = Math.addExact(this, other)
public infix fun Long.plusExact(other : Long) : Long = Math.addExact(this, other)
public infix fun Int.minusExact(other : Int) : Int = Math.subtractExact(this, other)
public infix fun Long.minusExact(other : Long) : Long = Math.subtractExact(this, other)
public infix fun Int.timesExact(other : Int) : Int = Math.multiplyExact(this, other)
public infix fun Long.timesExact(other : Long) : Long = Math.multiplyExact(this, other)
public fun Int.negateExact() : Int = Math.negateExact(this)
public fun Long.negateExact() : Long = Math.negateExact(this)
public fun Int.absExact() : Int = Math.absExact(this)
public fun Long.absExact() : Long = Math.absExact(this)
public fun Long.toIntExact() : Int = Math.toIntExact(this)


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
