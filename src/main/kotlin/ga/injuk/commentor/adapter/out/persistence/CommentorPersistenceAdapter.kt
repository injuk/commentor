package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.core.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.core.exception.UncaughtException
import ga.injuk.commentor.adapter.out.dto.FindByResponseDto
import ga.injuk.commentor.adapter.out.dto.FindOneResponseDto
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.out.persistence.*
import ga.injuk.commentor.common.annotation.Adapter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.By
import ga.injuk.commentor.domain.model.Comment
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentorRepository: CommentorRepository,
): CreateCommentPort, GetCommentPort, ListCommentsPort, ListSubCommentsPort, UpdateCommentPort, DeleteCommentPort, BulkDeleteCommentPort {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun create(user: User, request: CreateCommentRequest): Long {
        val (insertId) = commentorRepository.insert(user, request)
        if(insertId == null) {
            throw UncaughtException("failed to create comment resource.")
        }

        return insertId
    }

    override fun get(user: User, request: GetCommentRequest): Comment? {
        val commentRow = commentorRepository.findOne(request)

        return commentRow?.let { row ->
            tryConvertToComment(row)
                .getOrElse {
                    logger.error(it.message)
                    throw UncaughtException("failed to convert comment")
                }
        }
    }

    override fun getList(user: User, request: ListCommentsRequest): Pagination<Comment> {
        val (commentRows, cursor) = commentorRepository.findBy(user, request)

        val results = commentRows.map { row ->
            tryConvertToComment(row).getOrElse {
                logger.error(it.message)
                throw UncaughtException("failed to convert comment")
            }
        }

        return Pagination(
            results = results,
            nextCursor = cursor,
        )
    }

    override fun getList(user: User, request: ListSubCommentsRequest): Pagination<Comment> {
        val (commentRows, cursor) = commentorRepository.findBy(user, request)

        val results = commentRows.map { row ->
            tryConvertToComment(row).getOrElse {
                logger.error(it.message)
                throw UncaughtException("failed to convert comment")
            }
        }

        return Pagination(
            results = results,
            nextCursor = cursor,
        )
    }

    override fun update(user: User, request: UpdateCommentRequest): Int {
        val affectedRows = commentorRepository.update(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to update comment")
    }

    override fun delete(user: User, request: DeleteCommentRequest): Int {
        val affectedRows = commentorRepository.delete(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to delete comment")
    }

    override fun delete(request: BulkDeleteCommentRequest): Int {
        val affectedRows = commentorRepository.deleteBy(request)

        return affectedRows.count ?: throw UncaughtException("failed to delete comment")
    }

    private fun <T> tryConvertToComment(comment: T) = runCatching {
        when(comment) {
            is FindOneResponseDto -> Comment(
                id = comment.id ?: throw InvalidArgumentException("id cannot be null"),
                parts = comment.parts ?: throw InvalidArgumentException("parts cannot be null"),
                isDeleted = comment.isDeleted ?: throw InvalidArgumentException("isDeleted cannot be null"),
                hasSubComments = comment.hasSubComments ?: throw InvalidArgumentException("hasSubComments cannot be null"),
                likeCount = comment.likeCount ?: throw InvalidArgumentException("likeCount cannot be null"),
                dislikeCount = comment.dislikeCount ?: throw InvalidArgumentException("dislikeCount cannot be null"),
                created = Comment.Context(
                    at = comment.createdAt ?: throw InvalidArgumentException("createdAt cannot be null"),
                    by = By(
                        id = comment.createdBy ?: throw InvalidArgumentException("createdById cannot be null"),
                    ),
                ),
                updated = Comment.Context(
                    at = comment.updatedAt ?: throw InvalidArgumentException("updatedAt cannot be null"),
                    by = By(
                        id = comment.updatedBy ?: throw InvalidArgumentException("updatedById cannot be null"),
                    ),
                ),
            )

            is FindByResponseDto.Row -> Comment(
                id = comment.id ?: throw InvalidArgumentException("id cannot be null"),
                parts = comment.parts ?: throw InvalidArgumentException("parts cannot be null"),
                isDeleted = comment.isDeleted ?: throw InvalidArgumentException("isDeleted cannot be null"),
                hasSubComments = comment.hasSubComments ?: throw InvalidArgumentException("hasSubComments cannot be null"),
                likeCount = comment.likeCount ?: throw InvalidArgumentException("likeCount cannot be null"),
                dislikeCount = comment.dislikeCount ?: throw InvalidArgumentException("dislikeCount cannot be null"),
                created = Comment.Context(
                    at = comment.createdAt ?: throw InvalidArgumentException("createdAt cannot be null"),
                    by = By(
                        id = comment.createdBy ?: throw InvalidArgumentException("createdById cannot be null"),
                    ),
                ),
                updated = Comment.Context(
                    at = comment.updatedAt ?: throw InvalidArgumentException("updatedAt cannot be null"),
                    by = By(
                        id = comment.updatedBy ?: throw InvalidArgumentException("updatedById cannot be null"),
                    ),
                ),
            )

            else -> throw InvalidArgumentException("invalid instance")
        }
    }
}