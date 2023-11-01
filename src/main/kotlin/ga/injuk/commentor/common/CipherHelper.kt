package ga.injuk.commentor.common

import org.apache.tomcat.util.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

internal object CipherHelper {
    private const val SALT = "PpOoNnMmLlKkJjIiHhGgFfEeDdCcBbAa"
    private const val ALGORITHM = "AES"

    private val secretKey = SecretKeySpec(SALT.toByteArray(), ALGORITHM)
    private val cipherInstance = Cipher.getInstance(ALGORITHM)

    fun encode(plain: String?): String? = plain?.let {
        cipherInstance.run {
            init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = doFinal(plain.toByteArray())

            Base64.encodeBase64String(encryptedBytes)
        }
    }

    fun decode(cipher: String?): String? = cipher?.let {
        cipherInstance.run {
            init(Cipher.DECRYPT_MODE, secretKey)
            val decryptedBytes = doFinal(Base64.decodeBase64(cipher))

            String(decryptedBytes)
        }
    }
}