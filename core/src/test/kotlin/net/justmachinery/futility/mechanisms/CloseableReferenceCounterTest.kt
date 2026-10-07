package net.justmachinery.futility.mechanisms

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CloseableReferenceCounterTest {
    private class Resource : AutoCloseable {
        var closed = 0
        override fun close(){ closed += 1 }
    }

    @Test
    fun closesValueWhenLastUseReleased(){
        val resource = Resource()
        val counter = CloseableReferenceCounter(resource)
        assertTrue(counter.tryAcquireUse())
        counter.close()
        assertEquals(0, resource.closed)
        counter.releaseUse()
        assertEquals(1, resource.closed)
    }

    @Test
    fun cannotAcquireAfterFullyReleased(){
        val resource = Resource()
        val counter = CloseableReferenceCounter(resource)
        counter.close()
        assertEquals(1, resource.closed)
        assertFalse(counter.tryAcquireUse())
        assertFailsWith<IllegalStateException> { counter.releaseUse() }
    }

    @Test
    fun tryUseRunsAgainstLiveValue(){
        val resource = Resource()
        val counter = CloseableReferenceCounter(resource)
        assertEquals(42, counter.tryUse { 42 }.justOrNull())
        assertEquals(0, resource.closed)
        counter.close()
        assertEquals(null, counter.tryUse { 42 }.justOrNull())
    }
}
