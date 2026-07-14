package net.justmachinery.futility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MaybeTest {
    @Test
    fun justOrNullReturnsValueOrNull() {
        assertEquals(5, Maybe.Just(5).justOrNull())
        assertNull(Maybe.Nothing<Int>().justOrNull())
    }

    @Test
    fun orReturnsFallbackOnlyForNothing() {
        assertEquals(5, Maybe.Just(5).or(99))
        assertEquals(99, Maybe.Nothing<Int>().or(99))
    }

    @Test
    fun orLazyDoesNotEvaluateFallbackForJust() {
        var evaluated = false
        val value = Maybe.Just(5).or { evaluated = true; 99 }
        assertEquals(5, value)
        assertEquals(false, evaluated)
    }

    @Test
    fun orLazyEvaluatesFallbackForNothing() {
        assertEquals(99, Maybe.Nothing<Int>().or { 99 })
    }

    @Test
    fun justOrThrowReturnsValueForJust() {
        assertEquals(5, Maybe.Just(5).justOrThrow { IllegalStateException() })
    }

    @Test
    fun justOrThrowThrowsForNothing() {
        assertFailsWith<IllegalArgumentException> {
            Maybe.Nothing<Int>().justOrThrow { IllegalArgumentException("empty") }
        }
    }
}
