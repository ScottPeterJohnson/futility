package net.justmachinery.futility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumbersTest {
    @Test
    fun clampToIntSaturatesOutOfRangeLongs() {
        assertEquals(Int.MAX_VALUE, Long.MAX_VALUE.clampToInt())
        assertEquals(Int.MIN_VALUE, Long.MIN_VALUE.clampToInt())
        assertEquals(Int.MAX_VALUE, (Int.MAX_VALUE.toLong() + 1).clampToInt())
        assertEquals(Int.MIN_VALUE, (Int.MIN_VALUE.toLong() - 1).clampToInt())
    }

    @Test
    fun clampToIntPassesThroughInRangeLongs() {
        assertEquals(0, 0L.clampToInt())
        assertEquals(42, 42L.clampToInt())
        assertEquals(-42, (-42L).clampToInt())
        assertEquals(Int.MAX_VALUE, Int.MAX_VALUE.toLong().clampToInt())
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE.toLong().clampToInt())
    }

    @Test
    fun divRoundUpRoundsTowardsPositiveInfinityOnRemainder() {
        assertEquals(4L, 10L.divRoundUp(3L))
        assertEquals(3L, 9L.divRoundUp(3L))
        assertEquals(0L, 0L.divRoundUp(3L))
        assertEquals(1L, 1L.divRoundUp(3L))

        assertEquals(4, 10.divRoundUp(3))
        assertEquals(3, 9.divRoundUp(3))
    }

    @Test
    fun squaredMultipliesByItself() {
        assertEquals(9, 3.squared())
        assertEquals(9, (-3).squared())
        assertEquals(9L, 3L.squared())
        assertEquals(0, 0.squared())
    }

    @Test
    fun sqrtComputesSquareRoot() {
        assertEquals(3.0, 9.0.sqrt(), 1e-9)
        assertEquals(2.0f, 4.0f.sqrt(), 1e-6f)
    }

    @Test
    fun minAndMaxPickTheSmallerAndLarger() {
        assertEquals(1, 1.min(2))
        assertEquals(1, 2.min(1))
        assertEquals(2, 1.max(2))
        assertEquals(2, 2.max(1))

        assertEquals(-1L, 1L.min(-1L))
        assertEquals(1.0, 1.0.min(2.0), 1e-9)
        assertEquals(2.0f, 1.0f.max(2.0f), 1e-6f)
    }

    @Test
    fun isWithinComparesAgainstTheOtherValue() {
        assertTrue(10.isWithin(12, 2))
        assertTrue(14.isWithin(12, 2))
        assertFalse(9.isWithin(12, 2))
        assertFalse(15.isWithin(12, 2))

        assertTrue(10L.isWithin(11L, 1L))
        assertTrue(1.05.isWithin(1.0, 0.1))
        assertFalse(1.05f.isWithin(1.0f, 0.01f))
    }
}
