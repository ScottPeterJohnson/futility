package net.justmachinery.futility.primitives

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private object MetersUnit : Magnitude<MetersUnit>, MultipliesTo<MetersUnit, SquareMetersUnit>,
    DividesTo<SecondsUnit, MetersPerSecondUnit>, DividesTo2<MetersPerSecondUnit, SecondsUnit>
private typealias Meters = DoubleWrapper<MetersUnit>
private val Double.meters get() = Meters(this)

private object SquareMetersUnit : Magnitude<SquareMetersUnit>
private typealias SquareMeters = DoubleWrapper<SquareMetersUnit>
private val Double.squareMeters get() = SquareMeters(this)

private object SecondsUnit : Magnitude<SecondsUnit>
private typealias Seconds = DoubleWrapper<SecondsUnit>
private val Double.seconds get() = Seconds(this)

private object MetersPerSecondUnit : Magnitude<MetersPerSecondUnit>, MultipliesTo<SecondsUnit, MetersUnit>
private typealias MetersPerSecond = DoubleWrapper<MetersPerSecondUnit>
private val Double.metersPerSecond get() = MetersPerSecond(this)

private object ScaleUnit : Factor<ScaleUnit>
private typealias Scale = DoubleWrapper<ScaleUnit>
private val Double.scale get() = Scale(this)

private object BytesUnit : Magnitude<BytesUnit>
private typealias Bytes = IntWrapper<BytesUnit>
private val Int.bytes get() = Bytes(this)

private object MillisUnit : Magnitude<MillisUnit>
private typealias Millis = LongWrapper<MillisUnit>
private val Long.millis get() = Millis(this)

class WrappersTest {
    @Test
    fun addsAndSubtractsWithinAUnit() {
        assertEquals(5.0.meters, 2.0.meters + 3.0.meters)
        assertEquals((-1.0).meters, 2.0.meters - 3.0.meters)
        assertEquals(2.0.meters, ((-2.0).meters).abs())
        assertEquals(6.0.meters, listOf(1.0.meters, 2.0.meters, 3.0.meters).sum())
    }

    @Test
    fun scalesByRawNumbers() {
        assertEquals(6.0.meters, 2.0.meters * 3.0)
        assertEquals(6.0.meters, 2.0.meters * 3)
        assertEquals(1.0.meters, 2.0.meters / 2)
    }

    @Test
    fun integerScalingRoundsExplicitly() {
        assertEquals(6.bytes, 2.bytes * 3)
        assertEquals(1.bytes, 7.bytes % 2)
        assertEquals(4.bytes, 7.bytes.divRoundUp(2))
        assertEquals(3.bytes, 6.bytes.divRoundUp(2))
        assertEquals(4.bytes, 3.bytes.timesUp(1.1))
        assertEquals(3.bytes, 3.bytes.timesDown(1.1))
        assertEquals(6.bytes, listOf(1.bytes, 2.bytes, 3.bytes).sum())
    }

    @Test
    fun clampToIntKeepsTheUnit() {
        assertEquals(IntWrapper<MillisUnit>(5), 5L.millis.clampToInt())
        assertEquals(IntWrapper<MillisUnit>(Int.MAX_VALUE), Long.MAX_VALUE.millis.clampToInt())
    }

    /** The point of the numbered variants: one unit taking part in more than one relation. */
    @Test
    fun multipliesAndDividesAcrossUnits() {
        val speed = 10.0.meters / 2.0.seconds
        assertEquals(5.0.metersPerSecond, speed)
        assertEquals(2.0.seconds, 10.0.meters / speed)
        assertEquals(10.0.meters, speed * 2.0.seconds)
    }

    @Test
    fun dividingByItselfGivesARawRatio() {
        assertEquals(4.0, 10.0.meters / 2.5.meters, 1e-9)
        assertEquals(3, 6.bytes / 2.bytes)
        // Still resolves to the dimensioned overload when the units differ.
        assertEquals(5.0.metersPerSecond, 10.0.meters / 2.0.seconds)
    }

    @Test
    fun squaresIntoTheResultUnit() {
        assertEquals(9.0.squareMeters, 3.0.meters.squared())
        assertEquals(4.0.scale, 2.0.scale.squared())
    }

    @Test
    fun orders() {
        assertTrue(1.0.meters < 2.0.meters)
        assertEquals(1.0.meters, 1.0.meters.atMost(2.0.meters))
        assertEquals(2.0.meters, 1.0.meters.atLeast(2.0.meters))
        assertEquals(2.0.meters, 5.0.meters.clamp(0.0.meters, 2.0.meters))
        assertEquals(1.bytes, listOf(3.bytes, 1.bytes).minOrNull())
        assertEquals(3.bytes, listOf(3.bytes, 1.bytes).maxOrNull())
    }

    @Test
    fun isWithinComparesAgainstTheOtherValue() {
        assertTrue(10.0.meters.isWithin(12.0.meters, 2.0.meters))
        assertFalse(9.0.meters.isWithin(12.0.meters, 2.0.meters))
    }

    @Test
    fun lerps() {
        assertEquals(2.5.meters, lerp(2.0.meters, 3.0.meters, 0.5))
    }
}
