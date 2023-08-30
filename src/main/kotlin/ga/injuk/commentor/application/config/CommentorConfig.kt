package ga.injuk.commentor.application.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CommentorConfig {

    @Value("\${converter.hash.salt}")
    lateinit var salt: String

    @Value("\${converter.hash.length}")
    lateinit var length: String

    @Value("\${converter.hash.alphabet}")
    lateinit var alphabet: String
}