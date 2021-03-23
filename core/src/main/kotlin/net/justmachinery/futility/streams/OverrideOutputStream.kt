package net.justmachinery.futility.streams

import java.io.OutputStream

/**
 * An output stream that wraps another output stream, allowing filter/override behavior
 */
public open class OverrideOutputStream(public val output : OutputStream) : OutputStream() {
    override fun write(b: Int): Unit = output.write(b)
    override fun close(): Unit = output.close()
    override fun flush(): Unit = output.flush()
    override fun write(b: ByteArray): Unit = output.write(b)
    override fun write(b: ByteArray, off: Int, len: Int): Unit = output.write(b, off, len)
}