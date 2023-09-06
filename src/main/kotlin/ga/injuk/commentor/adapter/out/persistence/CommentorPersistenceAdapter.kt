package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.exception.UncaughtException
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.application.port.out.persistence.ListCommentsPort
import ga.injuk.commentor.common.annotation.Adapter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentorRepository: CommentorRepository,
): CreateCommentPort, ListCommentsPort {
    init {
        LoggerFactory
            .getLogger(this.javaClass)
            .debug("commentorRepository={}", commentorRepository.javaClass);
    }

    override fun create(user: User, request: CreateCommentRequest): Long {
        return commentorRepository.insert(user, request) ?: throw UncaughtException("Failed to create comment resource.")
    }

    override fun getList(user: User, request: ListCommentsRequest): List<Comment> {
        return commentorRepository.findBy(user, request)
    }
}