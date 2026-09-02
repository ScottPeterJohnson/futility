package net.justmachinery.futility.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ExecutionGateTest {
    @Test
    fun callbacksRegisteredBeforeOpenFireInOrderOnOpen() {
        val gate = ExecutionGate<Int>()
        val seen = mutableListOf<String>()
        gate.whenOpen { seen.add("a$it") }
        gate.whenOpen { seen.add("b$it") }
        assertEquals(emptyList(), seen) // nothing runs until opened

        gate.open(5)
        assertEquals(listOf("a5", "b5"), seen)
    }

    @Test
    fun callbackRegisteredAfterOpenFiresImmediately() {
        val gate = ExecutionGate<Int>()
        gate.open(7)

        var got: Int? = null
        gate.whenOpen { got = it }
        assertEquals(7, got)
    }

    @Test
    fun openingTwiceThrows() {
        val gate = ExecutionGate<Int>()
        gate.open(1)
        assertFailsWith<IllegalStateException> { gate.open(2) }
    }

    @Test
    fun deliversNullValueToWaiters() {
        val gate = ExecutionGate<Int?>()
        var called = false
        var received: Int? = 99
        gate.whenOpen { received = it; called = true }
        gate.open(null)
        assertEquals(true, called)
        assertNull(received)
    }
}
