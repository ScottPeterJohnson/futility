package net.justmachinery.futility.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class SequenceTest {
    @Test
    fun zipAllPadsShorterSequenceWithNulls() {
        val zipped = sequenceOf(1, 2, 3).zipAll(sequenceOf("a", "b")).toList()
        assertEquals(listOf(1 to "a", 2 to "b", 3 to null), zipped)
    }

    @Test
    fun zipAllPadsFirstSequenceWithNulls() {
        val zipped = sequenceOf(1).zipAll(sequenceOf("a", "b")).toList()
        assertEquals(listOf(1 to "a", null to "b"), zipped)
    }

    @Test
    fun flattenConsumingFlattensAndSkipsEmpties() {
        val flattened = sequenceOf(listOf(1, 2), emptyList(), listOf(3)).flattenConsuming()
        assertEquals(listOf(1, 2, 3), flattened.toList())
    }
}
