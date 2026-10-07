package net.justmachinery.futility.collections

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals

class PartitionByMapNotNullTest {
    @Test
    fun partitionsInterleavedConsumption(){
        val (nulls, notNulls) = (0 until 100).asSequence().partitionByMapNotNull { if(it % 2 == 0) it * 10 else null }
        val nullIter = nulls.iterator()
        val notNullIter = notNulls.iterator()
        val odds = mutableListOf<Int>()
        val evens = mutableListOf<Int>()
        while(nullIter.hasNext() || notNullIter.hasNext()){
            if(nullIter.hasNext()){ odds.add(nullIter.next()) }
            if(notNullIter.hasNext()){ evens.add(notNullIter.next()) }
        }
        assertEquals((1 until 100 step 2).toList(), odds)
        assertEquals((0 until 100 step 2).map { it * 10 }, evens)
    }

    @Test
    fun supportsConcurrentConsumers(){
        val (nulls, notNulls) = (0 until 10_000).asSequence().partitionByMapNotNull { if(it % 2 == 0) it else null }
        var odds : List<Int>? = null
        var evens : List<Int>? = null
        val t1 = thread { odds = nulls.toList() }
        val t2 = thread { evens = notNulls.toList() }
        t1.join()
        t2.join()
        assertEquals((1 until 10_000 step 2).toList(), odds)
        assertEquals((0 until 10_000 step 2).toList(), evens)
    }

    @Test
    fun handlesNullElementsInSource(){
        val (nulls, notNulls) = sequenceOf("a", null, "b").partitionByMapNotNull { it }
        assertEquals(listOf("a", "b"), notNulls.toList())
        assertEquals(listOf(null), nulls.toList())
    }
}
