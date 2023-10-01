package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.exception.UncaughtException
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.out.persistence.*
import ga.injuk.commentor.common.annotation.Adapter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentorRepository: CommentorRepository,
): CreateCommentPort, ListCommentsPort, UpdateCommentPort, DeleteCommentPort, BulkDeleteCommentPort {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun create(user: User, request: CreateCommentRequest): Long
        = commentorRepository.insert(user, request) ?: throw UncaughtException("Failed to create comment resource.")

    override fun getList(user: User, request: ListCommentsRequest): Pagination<Comment>
        = commentorRepository.findBy(user, request)

    override fun update(user: User, request: UpdateCommentRequest): Int
        = commentorRepository.update(user, request)

    override fun delete(user: User, request: DeleteCommentRequest): Int
        = commentorRepository.delete(user, request)

    override fun delete(request: BulkDeleteCommentRequest): Int
        = commentorRepository.deleteBy(request)
}