package ga.injuk.commentor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CommentorApplication

fun main(args: Array<String>) {
    runApplication<CommentorApplication>(*args)
}
