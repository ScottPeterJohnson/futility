package net.justmachinery.futility.streams

import kotlinx.io.Buffer
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import net.justmachinery.futility.execution.runThread
import java.io.*

public fun InputStream.readToString(): String = this.readAllBytes().toString(Charsets.UTF_8)
public fun InputStream.readToStringAndClose(): String = use { readToString() }
public fun InputStream.readAllThenClose(): ByteArray = use { readAllBytes() }
//readNBytes rather than read: a single read() may legally return fewer bytes than requested mid-stream.
public fun InputStream.readExactByteArray(length : Int): ByteArray = this.readNBytes(length).also { if(it.size != length) throw EOFException("End of stream") }

@Deprecated("The JDK now provides this", ReplaceWith("this.skipNBytes(number)"))
public fun InputStream.skipExactBytes(number: Long): Unit = this.skipNBytes(number)

/**
 * Run InputStream through an output transformer to create a new input stream.
 */
public fun InputStream.wrap(
    bufferSize : Int = 8192,
    //Callback which may:
    // 1. write to provided output stream, then
    // 2. return a new output stream that transforms provided output stream, and can be fed the rest of the input
    chain : (OutputStream) -> OutputStream
) : InputStream {
    var inputExhausted = false
    val buffer = Buffer()
    val bufferInput = buffer.asInputStream()
    val pipeOut = chain(buffer.asOutputStream())
    return object : InputStream() {
        private fun check(num : Int){
            if(inputExhausted){ return }
            while(buffer.size <= num){
                val next = this@wrap.readNBytes(bufferSize)
                if(next.isEmpty()){
                    inputExhausted = true
                    pipeOut.close()
                    return
                } else {
                    pipeOut.write(next)
                }
            }
        }
        override fun read(): Int {
            check(1)
            return bufferInput.read()
        }
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            check(len)
            return bufferInput.read(b, off, len)
        }

        override fun close() {
            if(!inputExhausted){
                inputExhausted = true
                pipeOut.close()
            }
            this@wrap.close()
        }
    }
}

/**
 * Create a readable input stream from the results of writing to an output stream.
 * Note this requires an extra thread.
 */
public fun generateInputStream(asyncCb : (OutputStream) -> Unit) : InputStream {
    val pipeIn = ErrorPropagatingPipedInputStream()
    runThread {
        pipeIn.transferExceptions {
            pipeIn.out.use {
                asyncCb(it)
            }
        }
    }
    return pipeIn
}

private class ErrorPropagatingPipedInputStream : InputStream() {
    @Volatile private var wasClosed : Boolean = false
    private val pipe = PipedInputStream(16_192)
    val out = PipedOutputStream(pipe)
    @Volatile var outError : Exception? = null

    fun transferExceptions(cb : ()->Unit){
        try {
            cb()
        } catch(e : Exception){
            if(wasClosed){ throw e }
            else { outError = e }
        }
    }

    inline fun <T> convertThrow(cb : ()->T) : T {
        if(outError != null){
            val error: Exception  = outError!!
            outError = null
            throw error
        } else {
            return cb()
        }
    }
    override fun read(): Int = convertThrow { pipe.read() }
    override fun read(b: ByteArray): Int = convertThrow { pipe.read(b) }
    override fun read(b: ByteArray, off: Int, len: Int) = convertThrow { pipe.read(b, off, len) }
    override fun readAllBytes() = convertThrow { pipe.readAllBytes()!! }
    @Deprecated("Deprecated in Java")
    override fun skip(n: Long) = convertThrow { pipe.skip(n) }
    override fun readNBytes(b: ByteArray?, off: Int, len: Int) = convertThrow { pipe.readNBytes(b, off, len) }
    override fun available() = convertThrow { pipe.available() }
    override fun reset() = convertThrow { pipe.reset() }
    override fun close() = convertThrow { wasClosed = true; pipe.close() }
    override fun mark(readlimit: Int) = convertThrow { pipe.mark(readlimit) }
    override fun markSupported() = convertThrow { pipe.markSupported() }
    override fun transferTo(out: OutputStream?) = convertThrow { pipe.transferTo(out) }
}



