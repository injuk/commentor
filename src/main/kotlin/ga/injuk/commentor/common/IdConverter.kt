package ga.injuk.commentor.common

import org.hashids.Hashids

internal object IdConverter {
    private const val SALT = "injuk-commentor"
    private const val LENGTH = 24
    private const val ALPHABET = "aBcDeFgHiJkLmNoPqRsTuVwXyZAbCdEfGhIjKlMnOpQrStUvWxYz"

    private val converter = Hashids(SALT, LENGTH, ALPHABET)

    fun convert(plain: Long?): String? = plain?.let { converter.encode(it) }

    fun convert(cipher: String?): Long? = cipher?.let { converter.decode(it).firstOrNull() }
}