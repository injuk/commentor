package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.out.persistence.CommentAccessPort
import ga.injuk.commentor.common.annotation.Adapter
import ga.injuk.commentor.domain.User
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentorRepository: CommentorRepository,
): CommentAccessPort {
    init {
        LoggerFactory
            .getLogger(this.javaClass)
            .debug("commentorRepository={}", commentorRepository.javaClass);
    }

    override fun create(user: User, request: CreateCommentRequest): Long? {
        return commentorRepository.insert(user, request)
    }
}