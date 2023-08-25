package ga.injuk.commentor.application.service

import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CommentorService(
    private val createCommentPort: CreateCommentPort,
): CreateCommentUseCase {
    init {
        LoggerFactory
            .getLogger(this.javaClass)
            .debug("createCommentPort={}", createCommentPort.javaClass)
    }
}