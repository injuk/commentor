package ga.injuk.commentor.common

import ga.injuk.commentor.application.config.CommentorConfig
import org.hashids.Hashids
import org.springframework.stereotype.Component

@Component
class HashIdConverter(
    config: CommentorConfig,
): IdConverter {
    private val converterInstance = Hashids(
        config.salt,
        config.length.toInt(),
        config.alphabet,
    )

    override fun encode(plain: Long)
        = converterInstance.encode(plain)

    override fun decode(cipher: String)
        = converterInstance.decode(cipher).firstOrNull() ?: throw RuntimeException("디코드 불가능")
}