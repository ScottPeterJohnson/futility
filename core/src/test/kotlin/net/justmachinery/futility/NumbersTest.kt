package net.justmachinery.futility

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
