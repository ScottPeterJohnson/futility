package net.justmachinery.futility.bytes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Base64Test {
    @Test
    fun standardEncodeDecodeRoundTrip() {
        val original = "Hello, futility! 123"
        assertEquals(original, base64DecodeString(base64EncodeString(original)))
    }

    @Test
    fun standardEncodesToExpectedValue() {
        assertEquals("aGVsbG8=", base64EncodeString("hello"))
        assertEquals("hello", base64DecodeString("aGVsbG8="))
    }

    @Test
    fun urlEncodeDecodeRoundTrip() {
        // Bytes that produce '+' and '/' in the standard alphabet become '-' and '_' in the URL alphabet.
        val bytes = byteArrayOf(-1, -2, -3, -4, -5)
        val encoded = base64UrlEncodeBytes(bytes)
        assertTrue(!encoded.contains('+') && !encoded.contains('/'))
        assertTrue(bytes.contentEquals(base64UrlDecodeBytes(encoded)))
    }

    @Test
    fun urlStringRoundTrip() {
        val original = "https://example.com/path?a=1&b=2"
        assertEquals(original, base64UrlDecodeString(base64UrlEncodeString(original)))
    }
}
