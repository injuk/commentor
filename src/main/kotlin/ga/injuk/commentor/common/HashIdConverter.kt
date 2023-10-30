package ga.injuk.commentor.common

import ga.injuk.commentor.application.config.ConverterConfig
import org.hashids.Hashids
import org.springframework.stereotype.Component

@Component
class HashIdConverter(
    private val config: ConverterConfig,
): IdConverter {
    private val converterInstance by lazy {
        val (salt, length, alphabet) = config.hash

        return@lazy Hashids(salt, length, alphabet)
    }

    override fun encode(plain: Long)
        = converterInstance.encode(plain)

    override fun decode(cipher: String)
        = converterInstance.decode(cipher).firstOrNull()
}