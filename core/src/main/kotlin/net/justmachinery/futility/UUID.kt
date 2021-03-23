package net.justmachinery.futility

import java.nio.ByteBuffer
import java.util.*

public fun UUID.toByteArray() : ByteArray {
    val bytes = ByteArray(16)
    val bb = ByteBuffer.wrap(bytes)
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}