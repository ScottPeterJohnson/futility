package net.justmachinery.futility.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class IterablesTest {
    @Test
    fun chunkedBySplitsByWeight() {
        val chunks = listOf(1, 2, 3, 4).chunkedBy(desiredWeight = 3) { it.toLong() }.toList()
        assertEquals(listOf(listOf(1, 2), listOf(3), listOf(4)), chunks)
    }

    @Test
    fun chunkedByAlwaysEmitsAtLeastOneItemPerChunkEvenIfOverweight() {
        val chunks = listOf(5, 6).chunkedBy(desiredWeight = 3) { it.toLong() }.toList()
        assertEquals(listOf(listOf(5), listOf(6)), chunks)
    }

    @Test
    fun chunkedByOnEmptyYieldsNothing() {
        assertEquals(emptyList(), emptyList<Int>().chunkedBy(desiredWeight = 3) { it.toLong() }.toList())
    }

    @Test
    fun forEachRemoveVisitsAndEmptiesCollection() {
        val list = mutableListOf(1, 2, 3)
        val seen = mutableListOf<Int>()
        list.forEachRemove { seen.add(it) }
        assertEquals(listOf(1, 2, 3), seen)
        assertEquals(emptyList(), list)
    }

    @Test
    fun forEachRemoveIfRemovesMatching() {
        val list = mutableListOf(1, 2, 3, 4, 5)
        list.forEachRemoveIf { it % 2 == 0 }
        assertEquals(listOf(1, 3, 5), list)
    }

    @Test
    fun rotateLeftShiftsTowardsFront() {
        assertEquals(listOf(2, 3, 4, 1), listOf(1, 2, 3, 4).rotateLeft(1))
        assertEquals(listOf(3, 4, 1, 2), listOf(1, 2, 3, 4).rotateLeft(2))
    }

    @Test
    fun rotateRightShiftsTowardsBack() {
        assertEquals(listOf(4, 1, 2, 3), listOf(1, 2, 3, 4).rotateRight(1))
    }
}
