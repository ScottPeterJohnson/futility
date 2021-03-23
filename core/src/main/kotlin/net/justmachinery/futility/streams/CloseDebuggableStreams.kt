package net.justmachinery.futility.streams

import mu.KLogging
import net.justmachinery.futility.CleanerWatcher
import net.justmachinery.futility.globalCleaner
import java.io.InputStream
import java.io.OutputStream

/**
 * This input stream records where it was opened in a stack trace, and logs as an error if it is cleaned rather than correctly finalized
 */
public class CloseDebuggableInputStream(input : InputStream) : OverrideInputStream(input) {
    private companion object : KLogging()
    private val cw = CleanerWatcher(input)
    override fun close() {
        cw.close()
    }
}

/**
 * See [CloseDebuggableInputStream]
 */
public class CloseDebuggableOutputStream(output : OutputStream) : OverrideOutputStream(output) {
    private companion object : KLogging()

    private val cw = CleanerWatcher(output)
    override fun close() {
        cw.close()
    }
}