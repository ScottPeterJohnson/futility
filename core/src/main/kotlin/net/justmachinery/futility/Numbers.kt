package net.justmachinery.futility

import kotlin.math.*

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
 * Whether [this] is within [tolerance] of [other], inclusive.
 */
public fun Int.isWithin(other : Int, tolerance : Int) : Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Long.isWithin(other: Long, tolerance: Long): Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Double.isWithin(other: Double, tolerance: Double): Boolean = this in ((other - tolerance)..(other + tolerance))
public fun Float.isWithin(other: Float, tolerance: Float): Boolean = this in ((other - tolerance)..(other + tolerance))



//Aliases for java.lang.Math and kotlin's math (which generally uses java.lang.Math) where provided

public infix fun Long.ceilDiv(divisor : Long) : Long = Math.ceilDiv(this, divisor)
public infix fun Long.ceilDiv(divisor : Int) : Long = Math.ceilDiv(this, divisor)
public infix fun Int.ceilDiv(divisor : Int) : Int = Math.ceilDiv(this, divisor)


public infix fun Long.ceilMod(divisor : Long) : Long = Math.ceilMod(this, divisor)
public infix fun Long.ceilMod(divisor : Int) : Int = Math.ceilMod(this, divisor)
public infix fun Int.ceilMod(divisor : Int) : Int = Math.ceilMod(this, divisor)

@Deprecated("Use ceilDiv", ReplaceWith("this.ceilDiv(divisor)"))
public fun Long.divRoundUp(divisor : Long) : Long = ceilDiv(divisor)
@Deprecated("Use ceilDiv", ReplaceWith("this.ceilDiv(divisor)"))
public fun Int.divRoundUp(divisor : Int) : Int = ceilDiv(divisor)

public infix fun Int.plusExact(other : Int) : Int = Math.addExact(this, other)
public infix fun Long.plusExact(other : Long) : Long = Math.addExact(this, other)
public infix fun Int.minusExact(other : Int) : Int = Math.subtractExact(this, other)
public infix fun Long.minusExact(other : Long) : Long = Math.subtractExact(this, other)
public infix fun Int.timesExact(other : Int) : Int = Math.multiplyExact(this, other)
public infix fun Long.timesExact(other : Long) : Long = Math.multiplyExact(this, other)
public infix fun Long.timesExact(other : Int) : Long = Math.multiplyExact(this, other)
public infix fun Int.divideExact(other : Int) : Int = Math.divideExact(this, other)
public infix fun Long.divideExact(other : Long) : Long = Math.divideExact(this, other)
public infix fun Int.floorDivExact(other : Int) : Int = Math.floorDivExact(this, other)
public infix fun Long.floorDivExact(other : Long) : Long = Math.floorDivExact(this, other)
public infix fun Int.ceilDivExact(other : Int) : Int = Math.ceilDivExact(this, other)
public infix fun Long.ceilDivExact(other : Long) : Long = Math.ceilDivExact(this, other)
public fun Int.incrementExact() : Int = Math.incrementExact(this)
public fun Long.incrementExact() : Long = Math.incrementExact(this)
public fun Int.decrementExact() : Int = Math.decrementExact(this)
public fun Long.decrementExact() : Long = Math.decrementExact(this)
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
public fun Double.cbrt() : Double = cbrt(this)
public fun Float.cbrt() : Float = cbrt(this)

public fun Double.ceil() : Double = ceil(this)
public fun Float.ceil() : Float = ceil(this)
public fun Double.floor() : Double = floor(this)
public fun Float.floor() : Float = floor(this)
public fun Double.round() : Double = round(this)
public fun Float.round() : Float = round(this)

public fun Double.exp() : Double = exp(this)
public fun Float.exp() : Float = exp(this)
public fun Double.expm1() : Double = expm1(this)
public fun Float.expm1() : Float = expm1(this)
public fun Double.ln() : Double = ln(this)
public fun Float.ln() : Float = ln(this)
public fun Double.ln1p() : Double = ln1p(this)
public fun Float.ln1p() : Float = ln1p(this)
public fun Double.log10() : Double = log10(this)
public fun Float.log10() : Float = log10(this)


public fun Double.asin() : Double = asin(this)
public fun Double.acos() : Double = acos(this)
public fun Double.atan() : Double = atan(this)
public fun Double.atan2(x : Double) : Double = atan2(this, x)
public fun Double.sinh() : Double = sinh(this)
public fun Float.sinh() : Float = sinh(this)
public fun Double.cosh() : Double = cosh(this)
public fun Float.cosh() : Float = cosh(this)
public fun Double.tanh() : Double = tanh(this)
public fun Float.tanh() : Float = tanh(this)

public infix fun Double.hypot(other : Double) : Double = hypot(this, other)
public infix fun Float.hypot(other : Float) : Float = hypot(this, other)
public fun Double.fma(multiplier : Double, addend : Double) : Double = Math.fma(this, multiplier, addend)
public fun Float.fma(multiplier : Float, addend : Float) : Float = Math.fma(this, multiplier, addend)
public infix fun Double.scalb(scaleFactor : Int) : Double = Math.scalb(this, scaleFactor)
public infix fun Float.scalb(scaleFactor : Int) : Float = Math.scalb(this, scaleFactor)
public fun Double.getExponent() : Int = Math.getExponent(this)
public fun Float.getExponent() : Int = Math.getExponent(this)

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
