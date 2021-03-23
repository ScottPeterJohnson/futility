package net.justmachinery.futility.bytes

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.charset.Charset
import kotlin.reflect.KClass


public fun ByteArray.interpretAsString(charset: Charset = Charsets.UTF_8): String = this.toString(charset)

/**
 * Overrides equals and hashCode to hold byte arrays
 */
@Serializable
@SerialName("ddc876cf-045d-444a-a4ba-e1d6bc666727")
public class ByteArrayWrapper(public val bytes : ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayWrapper

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}

public object ByteArrayWrapperSerializer : KSerializer<ByteArrayWrapper> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ddc876cf-045d-444a-a4ba-e1d6bc666727", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): ByteArrayWrapper {
        val bytes = decoder.decodeString().asHexBytes()
        return ByteArrayWrapper(bytes)
    }
    override fun serialize(encoder: Encoder, value: ByteArrayWrapper) {
        encoder.encodeString(value.bytes.toHex())
    }
}

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
public fun ByteArray.toHex(): String {
    val hexChars = CharArray(size * 2)
    for (j in indices) {
        val v = this[j].toInt() and 0xFF
        hexChars[j * 2] = HEX_ARRAY[v.ushr(4)]
        hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }
    return String(hexChars)
}

private val hexValue = HEX_ARRAY.withIndex().associateBy({ it.value }, { it.index })
public fun String.asHexBytes() : ByteArray {
    require(length % 2 == 0)
    val bytes = ByteArray(length / 2)
    for (i in (0 until length).step(2)){
        val upper = hexValue[this[i].toUpperCase()]!!
        val lower = hexValue[this[i+1].toUpperCase()]!!
        bytes[i/2] = (upper * 16 + lower).toByte()
    }
    return bytes
}
