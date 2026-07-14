package net.justmachinery.futility.strings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StringTest {
    @Test
    fun ellipsizeAfterTruncatesLongStrings() {
        assertEquals("hel...", "hello world".ellipsizeAfter(3))
    }

    @Test
    fun ellipsizeAfterLeavesShortStringsAlone() {
        assertEquals("hi", "hi".ellipsizeAfter(5))
        assertEquals("hello", "hello".ellipsizeAfter(5))
    }

    @Test
    fun hashCodeLongIsDeterministic() {
        assertEquals("futility".hashCodeLong(), "futility".hashCodeLong())
    }

    @Test
    fun hashCodeLongDiffersForDifferentStrings() {
        assertNotEquals("futility".hashCodeLong(), "utility".hashCodeLong())
    }
}
