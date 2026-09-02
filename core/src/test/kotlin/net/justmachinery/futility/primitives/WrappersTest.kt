package net.justmachinery.futility.primitives

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private object MetersUnit : AdditiveWith<MetersUnit>, RawScalable, Ordered,
    DividesTo<SecondsUnit, MetersPerSecondUnit>, DividesTo2<MetersPerSecondUnit, SecondsUnit>
private typealias Meters = DoubleWrapper<MetersUnit>
private val Double.meters get() = Meters(this)

private object SecondsUnit : AdditiveWith<SecondsUnit>, RawScalable, Ordered
private typealias Seconds = DoubleWrapper<SecondsUnit>
private val Double.seconds get() = Seconds(this)

private object MetersPerSecondUnit : AdditiveWith<MetersPerSecondUnit>, RawScalable, MultipliesTo<SecondsUnit, MetersUnit>
private typealias MetersPerSecond = DoubleWrapper<MetersPerSecondUnit>
private val Double.metersPerSecond get() = MetersPerSecond(this)

private object BytesUnit : AdditiveWith<BytesUnit>, RawScalable, Ordered
private typealias Bytes = IntWrapper<BytesUnit>
private val Int.bytes get() = Bytes(this)

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
        assertEquals(4.bytes, 3.bytes.timesUp(1.1))
        assertEquals(3.bytes, 3.bytes.timesDown(1.1))
        assertEquals(6.bytes, listOf(1.bytes, 2.bytes, 3.bytes).sum())
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
    fun orders() {
        assertTrue(1.0.meters < 2.0.meters)
        assertEquals(1.0.meters, 1.0.meters.min(2.0.meters))
        assertEquals(2.0.meters, 1.0.meters.max(2.0.meters))
        assertEquals(2.0.meters, 5.0.meters.coerceIn(0.0.meters, 2.0.meters))
        assertEquals(1.bytes, listOf(3.bytes, 1.bytes).minOrNull())
        assertEquals(3.bytes, listOf(3.bytes, 1.bytes).maxOrNull())
    }

    @Test
    fun lerps() {
        assertEquals(2.5.meters, lerp(2.0.meters, 3.0.meters, 0.5))
    }
}
