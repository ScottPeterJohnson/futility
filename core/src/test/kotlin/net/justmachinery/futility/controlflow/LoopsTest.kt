package net.justmachinery.futility.controlflow

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LoopsTest {
    @Test
    fun repeatUntilNotNullReturnsFirstNonNullAndPassesIteration() {
        val result = repeatUntilNotNull { iteration -> if (iteration == 3) "done@$iteration" else null }
        assertEquals("done@3", result)
    }

    @Test
    fun conditionalRepeatReturnsDoneValue() {
        var calls = 0
        val result = conditionalRepeat<String> {
            calls += 1
            if (calls < 3) repeat else done("finished")
        }
        assertEquals("finished", result)
        assertEquals(3, calls)
    }

    @Test
    fun conditionalRepeatGivesUpAfterMaximumAttempts() {
        var calls = 0
        val result = conditionalRepeat<String>(maximumAttempts = 3) {
            calls += 1
            repeat
        }
        assertNull(result)
        assertEquals(3, calls)
    }

    @Test
    fun isLastRepeatSignalsFinalAttempt() {
        val lastFlags = mutableListOf<Boolean>()
        conditionalRepeat<String>(maximumAttempts = 3) {
            lastFlags.add(isLastRepeat)
            repeat
        }
        assertEquals(listOf(false, false, true), lastFlags)
    }

    @Test
    fun repeatOnThrowRetriesUntilSuccess() {
        var attempts = 0
        val result = repeatOnThrow(maximumAttempts = 5) {
            attempts += 1
            if (attempts < 3) throw IllegalStateException("boom")
            "ok"
        }
        assertEquals("ok", result)
        assertEquals(3, attempts)
    }

    @Test
    fun repeatOnThrowRethrowsNonMatchingThrowable() {
        assertFailsWith<IllegalArgumentException> {
            repeatOnThrow(maximumAttempts = 5, matchThrowable = { it is IllegalStateException }) {
                throw IllegalArgumentException("nope")
            }
        }
    }

    @Test
    fun repeatOnThrowThrowsLastErrorAfterExhaustingAttempts() {
        assertFailsWith<IllegalStateException> {
            repeatOnThrow(maximumAttempts = 2) { throw IllegalStateException("persistent") }
        }
    }

    @Test
    fun loopCollectAccumulatesUntilDone() {
        val collected = loopCollect<Int> {
            if (first) {
                addAll(listOf(1, 2))
                repeat
            } else {
                add(3)
                done
            }
        }
        assertEquals(listOf(1, 2, 3), collected)
    }

    @Test
    fun narrowUntilEmptyRunsUntilDrained() {
        val collection = mutableListOf(1, 2, 3)
        collection.narrowUntilEmpty { collection.removeAt(collection.size - 1) }
        assertEquals(emptyList(), collection)
    }

    @Test
    fun narrowUntilEmptyThrowsIfCollectionDoesNotShrink() {
        val collection = mutableListOf(1, 2, 3)
        assertFailsWith<IllegalStateException> {
            collection.narrowUntilEmpty { /* does nothing, size unchanged */ }
        }
    }
}
