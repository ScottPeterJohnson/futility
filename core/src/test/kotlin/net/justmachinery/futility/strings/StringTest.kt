package net.justmachinery.futility.strings

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
