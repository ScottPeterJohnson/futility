/**
 * A simple encrypt-compress scheme for somewhat secure use cases of passing data around externally
 */
package net.justmachinery.futility.strings


import net.justmachinery.futility.bytes.base64DecodeBytes
import net.justmachinery.futility.bytes.base64EncodeBytes
import net.justmachinery.futility.bytes.base64UrlDecodeBytes
import net.justmachinery.futility.bytes.base64UrlEncodeBytes
import net.justmachinery.futility.streams.readToString
import net.justmachinery.futility.streams.wrap
import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

public fun microEncodeBase64(input : String, secretKey: String) : String {
    return base64EncodeBytes(microEncode(input.byteInputStream(), secretKey).readAllBytes())
}

public fun microDecodeBase64(input : String, secretKey : String) : String {
    return microDecode(base64DecodeBytes(input).inputStream(), secretKey).readToString()
}

public fun microUrlEncodeBase64(input : String, secretKey: String) : String {
    return base64UrlEncodeBytes(microEncode(input.byteInputStream(), secretKey).readAllBytes())
}

public fun microUrlDecodeBase64(input : String, secretKey : String) : String {
    return microDecode(base64UrlDecodeBytes(input).inputStream(), secretKey).readToString()
}


public fun microEncode(input : InputStream, secretKey : String) : InputStream {
    val cipher = getDesCipher(secretKey, Cipher.ENCRYPT_MODE)
    return input.wrap {
        GZIPOutputStream(CipherOutputStream(it, cipher))
    }
}

public fun microDecode(input : InputStream, secretKey : String) : InputStream  {
    val cipher = getDesCipher(secretKey, Cipher.DECRYPT_MODE)
    return GZIPInputStream(CipherInputStream(input, cipher))
}

private fun getDesCipher(secretKey : String, mode : Int) : Cipher {
    val keySpec = DESKeySpec(secretKey.encodeToByteArray())
    val keyFactory = SecretKeyFactory.getInstance("DES")
    val key = keyFactory.generateSecret(keySpec)
    val cipher = Cipher.getInstance("DES")
    cipher.init(mode, key)
    return cipher
}