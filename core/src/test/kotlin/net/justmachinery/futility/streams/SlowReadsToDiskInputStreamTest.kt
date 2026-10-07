package net.justmachinery.futility.streams

import net.justmachinery.futility.bytes.KiB
import java.io.IOException
import java.io.InputStream
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class SlowReadsToDiskInputStreamTest {
    @Test
    fun roundTripsWithoutSpilling(){
        val data = Random.nextBytes(64.KiB)
        val stream = SlowReadsToDiskInputStream(data.inputStream(), maxMemoryBuffer = 512.KiB, chunkSize = 4096)
        try {
            assertContentEquals(data, stream.readAllBytes())
        } finally {
            stream.close()
        }
    }

    @Test
    fun roundTripsAfterSpillingToDisk(){
        val data = Random.nextBytes(256.KiB)
        val stream = SlowReadsToDiskInputStream(
            data.inputStream(),
            maxMemoryBuffer = 32.KiB,
            chunkSize = 4096,
            maxBufferWaitMillis = 100
        )
        try {
            //Give the reader thread time to fill the memory buffer, exhaust its wait budget, and spool to disk
            Thread.sleep(500)
            assertContentEquals(data, stream.readAllBytes())
        } finally {
            stream.close()
        }
    }

    @Test
    fun propagatesReadErrors(){
        val failing = object : InputStream() {
            private var count = 0
            override fun read(): Int {
                if(count >= 100){ throw IOException("Underlying failure") }
                count += 1
                return 1
            }
        }
        val stream = SlowReadsToDiskInputStream(failing, chunkSize = 4096)
        try {
            assertFailsWith<IOException> { stream.readAllBytes() }
        } finally {
            stream.close()
        }
    }
}
