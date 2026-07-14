package net.justmachinery.futility.primitives

import kotlin.test.Test
import kotlin.test.assertEquals

class PackedPrimitivesTest {
    @Test
    fun twoIntsRoundTripIncludingNegatives() {
        for ((high, low) in listOf(0 to 0, 1 to 2, -1 to -2, Int.MAX_VALUE to Int.MIN_VALUE, -5 to 12345)) {
            val packed = TwoIntsInALong(high, low)
            assertEquals(high, packed.high)
            assertEquals(low, packed.low)
        }
    }

    @Test
    fun fourShortsRoundTrip() {
        val packed = FourShortsInALong(1, -2, 30000, -30000)
        assertEquals(1.toShort(), packed.first)
        assertEquals((-2).toShort(), packed.second)
        assertEquals(30000.toShort(), packed.third)
        assertEquals((-30000).toShort(), packed.fourth)
    }

    @Test
    fun fourBytesRoundTrip() {
        val packed = FourBytesInAnInt(0, 127, -128, -1)
        assertEquals(0.toByte(), packed.first)
        assertEquals(127.toByte(), packed.second)
        assertEquals((-128).toByte(), packed.third)
        assertEquals((-1).toByte(), packed.fourth)
    }

    @Test
    fun intAndFloatRoundTrip() {
        val packed = IntAndFloatInALong(-42, 3.14f)
        assertEquals(-42, packed.int)
        assertEquals(3.14f, packed.float)
    }
}
