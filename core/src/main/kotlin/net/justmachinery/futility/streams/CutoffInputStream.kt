package net.justmachinery.futility.streams

import net.justmachinery.futility.clampToInt
import java.io.InputStream
import java.lang.Integer.min

/**
 * Inputstream that throws an exception if too many bytes are read.
 */
public class CutoffInputStream(
    private val input : InputStream,
    private val maxReadable: Long
) : InputStream() {
    private var total: Long = 0

    private fun checkBefore(len : Int) : Int {
        val maxReadable = min((maxReadable - total).clampToInt(), len)
        if (maxReadable <= 0 && len != 0) {
            throw InputStreamCutoffException()
        }
        return maxReadable
    }
    private fun checkAfter(lenRead : Int){
        if(lenRead >= 0){
            total += lenRead
        }
    }
    override fun read(): Int {
        checkBefore(1)
        val i = input.read()
        checkAfter(if(i >= 0) 1 else 0)
        return i
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val maxReadable = checkBefore(len)
        val i = input.read(b, off, maxReadable)
        checkAfter(i)
        return i
    }

    override fun close() {
        input.close()
    }
}

public class InputStreamCutoffException : RuntimeException()