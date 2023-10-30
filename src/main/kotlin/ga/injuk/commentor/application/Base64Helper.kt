package ga.injuk.commentor.application

import java.util.*

internal object Base64Helper {
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    internal fun encode(plain: String?) = plain?.let { String(encoder.encode(plain.toByteArray())) }

    internal fun decode(cipher: String?) = cipher?.let { String(decoder.decode(cipher)) }
}