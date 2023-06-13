package net.justmachinery.futility.streams

import mu.KLogging
import net.justmachinery.futility.bytes.KiB
import net.justmachinery.futility.execution.runThread
import net.justmachinery.futility.swallowExceptions
import java.io.IOException
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Files
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * "Wraps" an input stream, consuming it as fast as possible in a background thread that pushes what it reads into a buffer.
 * If the buffer fills, the remainder of the input stream will be spooled to a temporary file on disk, and read from there.
 * This is intended to effectively manage an expensive-to-keep-open input stream by consuming it as fast as possible, even
 * if the user does not make good use of it.
 * @param maxMemoryBuffer Maximum number of bytes to buffer in memory before switching to disk
 * @param chunkSize Size of internal memory/disk chunks
 * @param maxBufferWaitMillis Maximum amount of cumulative time to wait for the reader to read when the buffer is full
 */
public class SlowReadsToDiskInputStream(
    private val input : InputStream,
    private val maxMemoryBuffer : Int = 512.KiB,
    private val chunkSize : Int = 16_384,
    private val maxBufferWaitMillis : Int = 4000
) : InputStream(){
    private val maxBufferedChunks get() = maxMemoryBuffer / chunkSize
    private companion object : KLogging()
    private sealed class MemoryBuffer {
        class MemoryBytes(val bytes : ByteArray) : MemoryBuffer()
        object Done : MemoryBuffer()
    }
    private sealed class DiskBuffer {
        class DiskBytes(val count : Int) : DiskBuffer()
        object Done : DiskBuffer()
    }
    private sealed class FinalResult {
        object Done : FinalResult()
        data class Exception(val throwable : Throwable) : FinalResult()
    }

    private val memoryBuffer = LinkedBlockingQueue<MemoryBuffer>(maxBufferedChunks)
    private val diskBuffer = LinkedBlockingQueue<DiskBuffer>()
    private val finalResult = LinkedBlockingQueue<FinalResult>()
    private val tempFile = lazy { Files.createTempFile("tmp", "slowinpcache") }
    private val fileInput = lazy { tempFile.value.toFile().inputStream() }

    private val inputProxy = SequenceInputStream(object : Enumeration<InputStream> {
        //See the constructor of SequenceInputStream, which immediately tries to get an input stream.
        //If not accounted for, that would block immediately upon construction.
        private var gaveFirst = false
        private var memoryDone = false
        private var diskDone = false
        private var isExhausted = false
        override fun hasMoreElements(): Boolean {
            return !isExhausted
        }

        override fun nextElement(): InputStream {
            if(!gaveFirst){
                gaveFirst = true
                return nullInputStream()
            }
            if(!memoryDone){
                when(val buffered = memoryBuffer.take()){
                    is MemoryBuffer.MemoryBytes -> {
                        return buffered.bytes.inputStream()
                    }
                    is MemoryBuffer.Done -> {
                        memoryDone = true
                    }
                }
            }
            if(!diskDone){
                when(val buffered = diskBuffer.take()){
                    is DiskBuffer.DiskBytes -> {
                        val bytes = fileInput.value.readNBytes(buffered.count)
                        bytes.inputStream()
                    }
                    is DiskBuffer.Done -> {
                        diskDone = true
                    }
                }
            }
            isExhausted = true
            when(val final = finalResult.take()){
                is FinalResult.Done -> {
                    return nullInputStream()
                }
                is FinalResult.Exception -> {
                    throw IOException(final.throwable)
                }
            }
        }
    })

    private val readerThread = runThread {
        var totalWaitTime = 0L
        var memoryOverflow : ByteArray? = null
        try {
            input.use {
                while(true){
                    val buf = input.readNBytes(chunkSize)
                    if(buf.isEmpty()){
                        break
                    }
                    val added : Boolean
                    val elapsed = measureTimeMillis {
                        added = memoryBuffer.offer(
                            MemoryBuffer.MemoryBytes(buf),
                            (maxBufferWaitMillis - totalWaitTime).coerceAtLeast(0),
                            TimeUnit.MILLISECONDS
                        )
                    }
                    totalWaitTime += elapsed
                    if(!added){
                        memoryOverflow = buf
                        break
                    }
                }
                //Since the buffer might be full (and it'd be a pain to do this otherwise), we'll tamper with the
                //queue to increase its capacity by 1.
                memoryBuffer.javaClass.getDeclaredField("capacity").also {
                    it.isAccessible = true
                }.set(memoryBuffer, maxBufferedChunks + 1)
                memoryBuffer.offer(MemoryBuffer.Done)

                if(memoryOverflow != null){
                    logger.warn { "Filled buffer, shunting to disk" }
                    tempFile.value.toFile().outputStream().use { out ->
                        val buffer = ByteArray(chunkSize)
                        memoryOverflow!!.copyInto(buffer)
                        var bytes = memoryOverflow!!.size
                        while (true) {
                            out.write(buffer, 0, bytes)
                            out.flush()
                            diskBuffer.add(DiskBuffer.DiskBytes(bytes))
                            bytes = input.read(buffer)
                            if(bytes < 0){ break }
                        }
                    }
                }
                diskBuffer.offer(DiskBuffer.Done)

                finalResult.offer(FinalResult.Done)
            }
        } catch(t : Throwable){
            memoryBuffer.offer(MemoryBuffer.Done)
            diskBuffer.offer(DiskBuffer.Done)
            finalResult.offer(FinalResult.Exception(t))
        }
    }

    override fun read(): Int {
        return inputProxy.read()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return inputProxy.read(b, off, len)
    }

    override fun close() {
        readerThread.cancel(true)
        if(tempFile.isInitialized()){
            swallowExceptions {
                Files.delete(tempFile.value)
            }
        }
        if(fileInput.isInitialized()){
            swallowExceptions {
                fileInput.value.close()
            }
        }
    }
}