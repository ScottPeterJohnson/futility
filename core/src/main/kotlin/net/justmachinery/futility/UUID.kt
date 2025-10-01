package net.justmachinery.futility

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.nio.ByteBuffer
import java.util.*

public fun UUID.toByteArray() : ByteArray {
    val bytes = ByteArray(16)
    val bb = ByteBuffer.wrap(bytes)
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

public object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("d051e1ce-5bf8-48b1-ad2d-3bb4c54371b1"){
        element<Long>("hi")
        element<Long>("lo")
    }
    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeStructure(descriptor){
            encodeLongElement(descriptor, 0, value.mostSignificantBits)
            encodeLongElement(descriptor, 1, value.leastSignificantBits)
        }
    }
    override fun deserialize(decoder: Decoder): UUID {
        return decoder.decodeStructure(descriptor){
            var hi = 0L
            var lo = 0L
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> hi = decodeLongElement(descriptor, 0)
                    1 -> lo = decodeLongElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            UUID(hi, lo)
        }
    }
}