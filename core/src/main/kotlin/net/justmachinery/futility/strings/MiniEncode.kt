/**
 * A simple compress-and-encrypt scheme for passing small blobs of data around externally,
 * e.g. in URLs or hidden form fields.
 *
 * Format: 12-byte random IV, then AES-256-GCM ciphertext+tag of the GZIP-compressed input.
 * The key is the SHA-256 of the secret key string, so the secret should be a strong random value.
 * Note that compressing before encrypting leaks some information about content length/redundancy;
 * don't use this where an attacker can mix their own data into the plaintext alongside secrets.
 */
package net.justmachinery.futility.strings

import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

private const val GCM_IV_BYTES = 12
private const val GCM_TAG_BITS = 128
private val secureRandom = SecureRandom()

public fun miniEncodeUrlBase64(input : String, secretKey : String) : String =
    Base64.UrlSafe.encode(miniEncode(input.encodeToByteArray(), secretKey))

public fun miniDecodeUrlBase64(input : String, secretKey : String) : String =
    miniDecode(Base64.UrlSafe.decode(input), secretKey).decodeToString()

public fun miniEncode(input : ByteArray, secretKey : String) : ByteArray {
    val iv = ByteArray(GCM_IV_BYTES).also { secureRandom.nextBytes(it) }
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, aesKey(secretKey), GCMParameterSpec(GCM_TAG_BITS, iv))
    val compressed = ByteArrayOutputStream().also { bytes ->
        GZIPOutputStream(bytes).use { it.write(input) }
    }.toByteArray()
    return iv + cipher.doFinal(compressed)
}

/**
 * Throws [javax.crypto.AEADBadTagException] if [input] was not produced by [miniEncode] with the same key.
 */
public fun miniDecode(input : ByteArray, secretKey : String) : ByteArray {
    require(input.size > GCM_IV_BYTES){ "Input too short to be miniEncoded" }
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, aesKey(secretKey), GCMParameterSpec(GCM_TAG_BITS, input, 0, GCM_IV_BYTES))
    val compressed = cipher.doFinal(input, GCM_IV_BYTES, input.size - GCM_IV_BYTES)
    return GZIPInputStream(compressed.inputStream()).use { it.readAllBytes() }
}

private fun aesKey(secretKey : String) =
    SecretKeySpec(MessageDigest.getInstance("SHA-256").digest(secretKey.encodeToByteArray()), "AES")
