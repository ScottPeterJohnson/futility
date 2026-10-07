package net.justmachinery.futility.collections

import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertEquals

class BitSetTest {
    @Test
    fun forEachTrueVisitsSetBitsInOrder() {
        val bits = BitSet().apply {
            set(1); set(3); set(5)
        }
        val seen = mutableListOf<Int>()
        bits.forEachTrue { seen.add(it) }
        assertEquals(listOf(1, 3, 5), seen)
    }
}
