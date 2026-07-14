package net.justmachinery.futility.bytes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByteArrayTest {
    @Test
    fun toHexUsesUpperCase() {
        assertEquals("00FF0A10", byteArrayOf(0, -1, 10, 16).toHex())
        assertEquals("", byteArrayOf().toHex())
    }

    @Test
    fun asHexBytesAcceptsBothCases() {
        assertTrue(byteArrayOf(0, -1, 10, 16).contentEquals("00ff0A10".asHexBytes()))
    }

    @Test
    fun hexRoundTrips() {
        val bytes = byteArrayOf(1, 2, 3, 127, -128, -1)
        assertTrue(bytes.contentEquals(bytes.toHex().asHexBytes()))
    }

    @Test
    fun interpretAsStringDecodesUtf8() {
        assertEquals("héllo", "héllo".toByteArray(Charsets.UTF_8).interpretAsString())
    }

    @Test
    fun byteArrayWrapperEqualityIsByContent() {
        val a = ByteArrayWrapper(byteArrayOf(1, 2, 3))
        val b = ByteArrayWrapper(byteArrayOf(1, 2, 3))
        val c = ByteArrayWrapper(byteArrayOf(1, 2, 4))

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertFalse(a == c)
    }
}
