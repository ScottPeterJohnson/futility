package net.justmachinery.futility.bytes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ByteArrayTest {
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
