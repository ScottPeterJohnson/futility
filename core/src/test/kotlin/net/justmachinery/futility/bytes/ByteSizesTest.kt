package net.justmachinery.futility.bytes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ByteSizesTest {
    @Test
    fun decimalSuffixesMultiply() {
        assertEquals(5000L, 5L.kb)
        assertEquals(5000, 5.kb)
        assertEquals(3_000_000L, 3L.mb)
        assertEquals(3_000_000, 3.mb)
        assertEquals(2_000_000_000L, 2L.gb)
    }

    @Test
    fun binarySuffixesMultiply() {
        assertEquals(1024L, 1L.KiB)
        assertEquals(1024, 1.KiB)
        assertEquals(1024L * 1024, 1L.MiB)
        assertEquals(1024 * 1024, 1.MiB)
        assertEquals(1024L * 1024 * 1024, 1L.GiB)
    }

    @Test
    fun overflowThrowsInsteadOfWrapping() {
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.kb }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE.kb }
        assertFailsWith<ArithmeticException> { Long.MAX_VALUE.GiB }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE.MiB }
    }
}
