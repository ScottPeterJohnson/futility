/**
 * Numeric primitive wrappers which come predefined with a number of operators.
 * The point is to make it easier to define something like e.g. Meters, and then
 * opt-in to those operations that make sense on it, without needing to rewrite
 * all the operator functions.
 *
 * The basic design is via inlined value classes which take a phantom type parameter <Type>.
 * Operator functions then become available (with import) for the interfaces that Type extends.
 * For serialization, annotate the tag type with [net.justmachinery.futility.NotSerializable].
 *
 * Example:
 * object MetersUnit : AdditiveWith<MetersUnit>, RawScalable, Ordered
 * typealias Meters = DoubleWrapper<MetersUnit>
 * val Double.meters get() = Meters(this)
 * val result = 1.0.meters + 2.0.meters
 *
 * The operators themselves are repetitive enough that they're generated, one file per wrapper
 */
package net.justmachinery.futility.primitives

import kotlinx.serialization.Serializable

//For the generated operations on these wrappers, see e.g. IntWrapperOperators

@Serializable
@JvmInline
public value class IntWrapper<@Suppress("unused") Unit>(public val raw : Int)

@Serializable
@JvmInline
public value class LongWrapper<@Suppress("unused") Unit>(public val raw : Long)

@Serializable
@JvmInline
public value class FloatWrapper<@Suppress("unused") Unit>(public val raw : Float)

@Serializable
@JvmInline
public value class DoubleWrapper<@Suppress("unused") Unit>(public val raw : Double)

/**
 * Units that can be multiplied by plain (raw) numbers. Note that IntWrappers can't be directly multiplied by floats; use explicit timesUp()/timesDown().
 */
public interface RawScalable

/** Units that can be ordered with themselves based on their raw numbers. */
public interface Ordered

/** Units that can be added to and subtracted from [OtherUnit]. */
public typealias AdditiveWith<OtherUnit> = AdditiveWith1<OtherUnit>
/** Units that, multiplied by [OtherUnit], produce [ResultUnit]. */
public typealias MultipliesTo<OtherUnit, ResultUnit> = MultipliesTo1<OtherUnit, ResultUnit>
/** Units that, divided by [OtherUnit], produce [ResultUnit]. */
public typealias DividesTo<OtherUnit, ResultUnit> = DividesTo1<OtherUnit, ResultUnit>
/** Units that, divided by [OtherUnit], produce a plain (raw) number: a dimensionless ratio. */
public typealias RawDividesTo<OtherUnit> = RawDividesTo1<OtherUnit>

/*
 * Combinations of the above for common cases.
 */
/**
 * A quantity like meters or bytes: adds to and subtracts from itself, scales by raw
 * numbers, orders against itself, and divides by itself into a plain ratio.
 */
public interface Magnitude<Self> : AdditiveWith<Self>, RawScalable, Ordered, RawDividesTo<Self>

/**
 * A [Magnitude] that is already dimensionless, so multiplying two of them stays in the same unit: a
 * scaling factor, a percentage.
 */
public interface Factor<Self> : Magnitude<Self>, MultipliesTo<Self, Self>

/*
 * Extra slots for unit operations if you need them:
 */

public interface AdditiveWith1<OtherUnit>
public interface AdditiveWith2<OtherUnit>
public interface AdditiveWith3<OtherUnit>
public interface AdditiveWith4<OtherUnit>

public interface MultipliesTo1<OtherUnit, ResultUnit>
public interface MultipliesTo2<OtherUnit, ResultUnit>
public interface MultipliesTo3<OtherUnit, ResultUnit>
public interface MultipliesTo4<OtherUnit, ResultUnit>

public interface DividesTo1<OtherUnit, ResultUnit>
public interface DividesTo2<OtherUnit, ResultUnit>
public interface DividesTo3<OtherUnit, ResultUnit>
public interface DividesTo4<OtherUnit, ResultUnit>

public interface RawDividesTo1<OtherUnit>
public interface RawDividesTo2<OtherUnit>
public interface RawDividesTo3<OtherUnit>
public interface RawDividesTo4<OtherUnit>