package net.justmachinery.futility.strings

import javax.crypto.AEADBadTagException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class MiniEncodeTest {
    private val key = "a not very secret key"

    @Test
    fun roundTrips(){
        val input = "Some data with some repetition repetition repetition, and some unicode: héllo ☃"
        assertEquals(input, miniDecodeUrlBase64(miniEncodeUrlBase64(input, key), key))
    }

    @Test
    fun producesDistinctCiphertextsPerCall(){
        assertNotEquals(miniEncodeUrlBase64("same", key), miniEncodeUrlBase64("same", key))
    }

    @Test
    fun rejectsTampering(){
        val encoded = miniEncode("payload".encodeToByteArray(), key)
        encoded[encoded.size - 1] = (encoded[encoded.size - 1].toInt() xor 1).toByte()
        assertFailsWith<AEADBadTagException> { miniDecode(encoded, key) }
    }
}
