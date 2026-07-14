package net.justmachinery.futility.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
    fun partitionByMapNotNullSplitsNullsFromResults() {
        val (nulls, results) = sequenceOf(1, 2, 3, 4, 5)
            .partitionByMapNotNull { if (it % 2 == 0) "even$it" else null }
        // Consuming the nulls sequence fully also fills the results buffer as a side effect.
        assertEquals(listOf(1, 3, 5), nulls.toList())
        assertEquals(listOf("even2", "even4"), results.toList())
    }

    @Test
    fun flattenConsumingFlattensAndSkipsEmpties() {
        val flattened = sequenceOf(listOf(1, 2), emptyList(), listOf(3)).flattenConsuming()
        assertEquals(listOf(1, 2, 3), flattened.toList())
    }

    @Test
    fun flattenConsumingCanOnlyBeIteratedOnce() {
        val flattened = sequenceOf(listOf(1, 2)).flattenConsuming()
        flattened.toList()
        assertFailsWith<IllegalStateException> { flattened.toList() }
    }
}
