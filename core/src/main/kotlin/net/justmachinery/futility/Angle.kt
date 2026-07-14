package net.justmachinery.futility

import kotlin.math.PI

/**
 * A wrapper around an angle stored in radians, distinguishing it at the type level from a plain [Double] and
 * from a value in degrees. Construct via the [deg]/[rad] extension accessors, e.g. `90.0.deg` or `1.5.rad`.
 */
@JvmInline
public value class Angle(public val raw : Double) {
    public fun sin() : Double = kotlin.math.sin(raw)
    public fun cos() : Double = kotlin.math.cos(raw)
    public fun tan() : Double = kotlin.math.tan(raw)

    /** This angle in radians. */
    public val radians : Double get() = raw
    /** This angle in degrees. */
    public val degrees : Double get() = raw / degToRad
}

private const val degToRad : Double = PI / 180.0

/** Interprets [this] as a number of degrees. */
public val Double.deg : Angle get() = Angle(this * degToRad)
/** Interprets [this] as a number of radians. */
public val Double.rad : Angle get() = Angle(this)
