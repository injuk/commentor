package ga.injuk.commentor.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "converter")
data class ConverterConfig(
    val hash: Hash,
) {
    data class Hash(
        val salt: String,
        val length: Int,
        val alphabet: String,
    )
}