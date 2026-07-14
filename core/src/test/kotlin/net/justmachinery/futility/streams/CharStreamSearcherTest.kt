package net.justmachinery.futility.streams

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CharStreamSearcherTest {
    @Test
    fun searchFindsPatternPresentInStream() {
        assertTrue(CharStreamSearcher(listOf("abc")).search("12abc34".reader()))
    }

    @Test
    fun searchReturnsFalseWhenAbsent() {
        assertFalse(CharStreamSearcher(listOf("abc")).search("123456".reader()))
    }

    @Test
    fun searchForMatchReportsPatternAndPosition() {
        val match = CharStreamSearcher(listOf("abc")).searchForMatch("12abc34".reader())
        assertEquals("abc", match?.pattern)
        assertEquals(2L, match?.startIndex)
        assertEquals(5L, match?.endIndex)
    }

    @Test
    fun searchForMatchReturnsNullWhenAbsent() {
        assertNull(CharStreamSearcher(listOf("zzz")).searchForMatch("abcdef".reader()))
    }

    @Test
    fun searchFindsEarliestOfMultiplePatterns() {
        val match = CharStreamSearcher(listOf("xyz", "cd")).searchForMatch("abcdefxyz".reader())
        assertEquals("cd", match?.pattern)
        assertEquals(2L, match?.startIndex)
    }

    @Test
    fun ignoreCaseMatchesRegardlessOfCase() {
        assertTrue(CharStreamSearcher(listOf("ABC"), ignoreCase = true).search("xxabcyy".reader()))
        assertFalse(CharStreamSearcher(listOf("ABC"), ignoreCase = false).search("xxabcyy".reader()))
    }

    @Test
    fun handlesOverlappingPrefixesViaKmp() {
        // "aab" only truly matches at the end; a naive matcher can trip on the repeated 'a'.
        val match = CharStreamSearcher(listOf("aab")).searchForMatch("aaab".reader())
        assertEquals(1L, match?.startIndex)
        assertEquals(4L, match?.endIndex)
    }
}
