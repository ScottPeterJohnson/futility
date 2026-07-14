package net.justmachinery.futility.streams

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CutoffInputStreamTest {
    private fun cutoff(bytes: ByteArray, limit: Long) = CutoffInputStream(bytes.inputStream(), limit)

    @Test
    fun readsFullyWhenUnderLimit() {
        val data = byteArrayOf(1, 2, 3)
        assertTrue(data.contentEquals(cutoff(data, 5).readAllBytes()))
    }

    @Test
    fun readsExactlyUpToLimitWithoutThrowing() {
        val data = byteArrayOf(1, 2, 3)
        // Source ends exactly at the limit -> EOF, no exception.
        assertTrue(data.contentEquals(cutoff(data, 3).readAllBytes()))
    }

    @Test
    fun throwsWhenMoreBytesThanLimitViaBulkRead() {
        val data = byteArrayOf(1, 2, 3, 4)
        assertFailsWith<InputStreamCutoffException> { cutoff(data, 3).readAllBytes() }
    }

    @Test
    fun throwsWhenMoreBytesThanLimitViaSingleByteRead() {
        val stream = cutoff(byteArrayOf(1, 2, 3, 4), 3)
        assertEquals(1, stream.read())
        assertEquals(2, stream.read())
        assertEquals(3, stream.read())
        assertFailsWith<InputStreamCutoffException> { stream.read() }
    }

    @Test
    fun zeroLengthReadReturnsZero() {
        val stream = cutoff(byteArrayOf(1, 2, 3), 3)
        assertEquals(0, stream.read(ByteArray(4), 0, 0))
    }
}
