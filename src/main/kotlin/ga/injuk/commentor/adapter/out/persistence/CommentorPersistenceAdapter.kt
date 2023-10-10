package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.core.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.core.exception.UncaughtException
import ga.injuk.commentor.adapter.out.dto.ListCommentsResponse
import ga.injuk.commentor.adapter.out.dto.GetCommentResponse
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.out.persistence.*
import ga.injuk.commentor.common.annotation.Adapter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.By
import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentInteraction
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentsDataAccess: CommentsDataAccess,
    private val commentInteractionsDataAccess: CommentInteractionsDataAccess,
): CreateCommentPort, GetCommentPort, ListCommentsPort, ListSubCommentsPort, UpdateCommentPort, DeleteCommentPort, BulkDeleteCommentPort,
CreateCommentInteractionPort, GetCommentInteractionPort, UpdateCommentInteractionPort, DeleteCommentInteractionPort {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun create(user: User, request: CreateCommentRequest): Long {
        val (insertId) = commentsDataAccess.insert(user, request)
        if(insertId == null) {
            throw UncaughtException("failed to create comment resource.")
        }

        return insertId
    }

    override fun get(user: User, request: GetCommentRequest): Comment? {
        val commentRow = commentsDataAccess.findOne(user, request)

        return commentRow?.let { row ->
            tryConvertToComment(row)
                .getOrElse {
                    logger.error(it.message)
                    throw UncaughtException("failed to convert comment")
                }
        }
    }

    override fun getList(user: User, request: ListCommentsRequest): Pagination<Comment> {
        val (commentRows, cursor) = commentsDataAccess.findBy(user, request)

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
        val (commentRows, cursor) = commentsDataAccess.findBy(user, request)

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
        val affectedRows = commentsDataAccess.update(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to update comment")
    }

    override fun delete(user: User, request: DeleteCommentRequest): Int {
        val affectedRows = commentsDataAccess.delete(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to delete comment")
    }

    override fun delete(request: BulkDeleteCommentRequest): Int {
        val affectedRows = commentsDataAccess.deleteBy(request)

        return affectedRows.count ?: throw UncaughtException("failed to delete comment")
    }

    override fun create(user: User, request: CreateCommentInteractionRequest): Long {
        val (insertId) = commentInteractionsDataAccess.insert(user, request)
        if(insertId == null) {
            throw UncaughtException("failed to create comment interaction resource.")
        }

        return insertId
    }

    override fun get(user: User, request: GetCommentInteractionRequest): CommentInteraction? {
        val commentInteractionRow = commentInteractionsDataAccess.findOne(user, request)

        return CommentInteraction(
            id = commentInteractionRow?.id ?: throw InvalidArgumentException("interaction id cannot be null"),
            commentId = commentInteractionRow.commentId ?: throw InvalidArgumentException("commentId cannot be null"),
            type = CommentInteractionType.from(commentInteractionRow.type) ?: throw InvalidArgumentException("interaction type cannot be null"),
            userId = commentInteractionRow.userId ?: throw InvalidArgumentException("userId cannot be null"),
        )
    }

    override fun update(user: User, request: UpdateCommentInteractionRequest): Int {
        val affectedRows = commentInteractionsDataAccess.update(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to upsert comment interaction")
    }

    override fun delete(user: User, request: DeleteCommentInteractionRequest): Int {
        val affectedRows = commentInteractionsDataAccess.delete(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to delete comment interaction")
    }

    private fun <T> tryConvertToComment(comment: T) = runCatching {
        when(comment) {
            is GetCommentResponse -> Comment(
                id = comment.id ?: throw InvalidArgumentException("id cannot be null"),
                parts = comment.parts ?: throw InvalidArgumentException("parts cannot be null"),
                isDeleted = comment.isDeleted ?: throw InvalidArgumentException("isDeleted cannot be null"),
                hasSubComments = comment.hasSubComments ?: throw InvalidArgumentException("hasSubComments cannot be null"),
                myInteraction = CommentInteractionType.from(comment.myInteractionType),
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

            is ListCommentsResponse.Row -> Comment(
                id = comment.id ?: throw InvalidArgumentException("id cannot be null"),
                parts = comment.parts ?: throw InvalidArgumentException("parts cannot be null"),
                isDeleted = comment.isDeleted ?: throw InvalidArgumentException("isDeleted cannot be null"),
                hasSubComments = comment.hasSubComments ?: throw InvalidArgumentException("hasSubComments cannot be null"),
                myInteraction = CommentInteractionType.from(comment.myInteractionType),
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