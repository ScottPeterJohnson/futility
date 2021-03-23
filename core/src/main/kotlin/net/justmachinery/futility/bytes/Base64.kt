package net.justmachinery.futility.bytes

import java.util.*

public fun base64EncodeString(data : String) : String {
    return base64EncodeBytes(data.toByteArray())
}
public fun base64EncodeBytes(data : ByteArray) : String {
    return Base64.getEncoder().encode(data).interpretAsString()
}
public fun base64DecodeString(data : String) : String {
    return base64DecodeBytes(data).interpretAsString()
}
public fun base64DecodeBytes(data : String) : ByteArray {
    return Base64.getDecoder().decode(data)
}
public fun base64UrlEncodeString(data : String) : String {
    return base64UrlEncodeBytes(data.toByteArray())
}
public fun base64UrlEncodeBytes(data : ByteArray) : String {
    return Base64.getUrlEncoder().encode(data).interpretAsString()
}
public fun base64UrlDecodeString(data : String) : String {
    return base64UrlDecodeBytes(data).interpretAsString()
}
public fun base64UrlDecodeBytes(data : String) : ByteArray {
    return Base64.getUrlDecoder().decode(data)
}