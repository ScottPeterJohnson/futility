package net.justmachinery.futility.streams

import java.io.EOFException
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class InputStreamsTest {
    //A stream that legally returns only one byte per read call even when more is available
    private fun trickle(data : ByteArray) = object : InputStream() {
        private var i = 0
        override fun read(): Int = if(i < data.size) data[i++].toInt() and 0xFF else -1
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            if(len == 0){ return 0 }
            if(i >= data.size){ return -1 }
            b[off] = data[i++]
            return 1
        }
    }

    @Test
    fun readExactByteArrayHandlesPartialReads(){
        val data = ByteArray(100){ it.toByte() }
        assertContentEquals(data, trickle(data).readExactByteArray(100))
    }

    @Test
    fun readExactByteArrayThrowsOnShortStream(){
        assertFailsWith<EOFException> { trickle(ByteArray(10)).readExactByteArray(20) }
    }
}
