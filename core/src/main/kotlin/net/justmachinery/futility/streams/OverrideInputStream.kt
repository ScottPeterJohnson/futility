package net.justmachinery.futility.streams

import java.io.InputStream
import java.io.OutputStream

/**
 * Unlike FilterInputStream and FilterOutputStream, these don't have hidden stupidities, like writing all the bytes
 * in a write(ByteArray,Int,Int) method one by one.
 * Note that if you override any of the read() methods, you probably want to override all of them.
 */
public open class OverrideInputStream(public val input: InputStream) : InputStream() {
    override fun read(): Int = input.read()
    override fun available(): Int = input.available()
    override fun close(): Unit = input.close()
    override fun mark(readlimit: Int): Unit = input.mark(readlimit)
    override fun markSupported(): Boolean = input.markSupported()
    override fun read(b: ByteArray?): Int = input.read(b)
    override fun read(b: ByteArray?, off: Int, len: Int): Int = input.read(b, off, len)
    override fun readAllBytes(): ByteArray = input.readAllBytes()
    override fun readNBytes(len: Int): ByteArray = input.readNBytes(len)
    override fun readNBytes(b: ByteArray?, off: Int, len: Int): Int = input.readNBytes(b, off, len)
    override fun reset(): Unit = input.reset()
    override fun skip(n: Long): Long = input.skip(n)
    override fun transferTo(out: OutputStream?): Long = input.transferTo(out)
    override fun skipNBytes(n: Long): Unit = input.skipNBytes(n)
}