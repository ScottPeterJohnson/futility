package net.justmachinery.futility.strings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MicroEncodeTest {
    // DES requires an 8-byte key.
    private val key = "s3cr3tk3"

    @Test
    fun base64RoundTrips() {
        val original = "The quick brown fox jumps over the lazy dog."
        val encoded = microEncodeBase64(original, key)
        assertNotEquals(original, encoded)
        assertEquals(original, microDecodeBase64(encoded, key))
    }

    @Test
    fun urlSafeBase64RoundTrips() {
        val original = "payload with symbols ?&=/+"
        val encoded = microUrlEncodeBase64(original, key)
        assertEquals(original, microUrlDecodeBase64(encoded, key))
    }

    @Test
    fun emptyStringRoundTrips() {
        assertEquals("", microDecodeBase64(microEncodeBase64("", key), key))
    }
}
