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
}
