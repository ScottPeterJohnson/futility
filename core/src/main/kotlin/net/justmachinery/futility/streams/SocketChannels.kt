package net.justmachinery.futility.streams

import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * Reads once into [buffer], throwing [EOFException] if the channel has reached end-of-stream.
 */
public fun SocketChannel.readOrThrow(buffer: ByteBuffer) {
    val read = read(buffer)
    if (read < 0) {
        throw EOFException("SocketChannel reached EOF with ${buffer.remaining()} bytes remaining")
    }
}

/**
 * Writes the entire remaining contents of [buffer], looping until nothing remains.
 */
public fun SocketChannel.writeExactly(buffer: ByteBuffer) {
    while (buffer.hasRemaining()) {
        write(buffer)
    }
}

/**
 * Reads until [buffer] is full, throwing [EOFException] if the channel ends first.
 */
public fun SocketChannel.readExactly(buffer: ByteBuffer) {
    while (buffer.hasRemaining()) {
        readOrThrow(buffer)
    }
}

/**
 * Reads exactly [bytes] bytes into a new array, throwing [EOFException] if the channel ends first.
 */
public fun SocketChannel.readExactly(bytes: Int): ByteArray {
    val result = ByteArray(bytes)
    val wrapped = ByteBuffer.wrap(result)
    readExactly(wrapped)
    return result
}

/**
 * Reads exactly four bytes and interprets them as a big-endian [Int].
 */
public fun SocketChannel.readInt(): Int {
    val wrapped = ByteBuffer.wrap(ByteArray(4))
    readExactly(wrapped)
    return wrapped.getInt(0)
}

/**
 * Writes [int] as four big-endian bytes.
 */
public fun SocketChannel.writeInt(int: Int) {
    val wrapped = ByteBuffer.allocate(4)
    wrapped.putInt(0, int)
    writeExactly(wrapped)
}
