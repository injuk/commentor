package ga.injuk.commentor.common

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

internal object CipherHelper {
    private const val SALT = "PpOoNnMmLlKkJjIiHhGgFfEeDdCcBbAa"
    private const val ALGORITHM = "AES"

    private val secretKey = SecretKeySpec(SALT.toByteArray(), ALGORITHM)
    private val cipherInstance = Cipher.getInstance(ALGORITHM)

    private val encoder = Base64.getUrlEncoder()
    private val decoder = Base64.getUrlDecoder()

    fun encode(plain: String?): String? = plain?.let {
        cipherInstance.run {
            init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = doFinal(plain.toByteArray())

            return@run encoder.encodeToString(encryptedBytes)
        }
    }

    fun decode(cipher: String?): String? = cipher?.let {
        cipherInstance.run {
            init(Cipher.DECRYPT_MODE, secretKey)
            val base64Decoded = decoder.decode(cipher)
            val decryptedBytes = doFinal(base64Decoded)

            return@run String(decryptedBytes)
        }
    }
}