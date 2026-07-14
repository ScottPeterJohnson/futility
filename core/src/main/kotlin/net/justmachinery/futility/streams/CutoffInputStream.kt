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
    private val remaining get() = maxReadable - total

    private fun hitLimitThrowIfMoreBytes() : Int {
        if(input.read() == -1){ return -1 }
        throw InputStreamCutoffException()
    }

    override fun read(): Int {
        if(remaining <= 0){ return hitLimitThrowIfMoreBytes() }
        val i = input.read()
        if(i >= 0){ total += 1 }
        return i
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if(len == 0){ return 0 }
        if(remaining <= 0){ return hitLimitThrowIfMoreBytes() }
        val i = input.read(b, off, min(remaining.clampToInt(), len))
        if(i > 0){ total += i }
        return i
    }

    override fun close() {
        input.close()
    }
}

public class InputStreamCutoffException : RuntimeException()