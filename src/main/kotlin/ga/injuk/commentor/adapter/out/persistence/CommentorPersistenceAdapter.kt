package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.core.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.core.exception.UncaughtException
import ga.injuk.commentor.adapter.out.persistence.dataAccess.CommentInteractionsDataAccess
import ga.injuk.commentor.adapter.out.persistence.dataAccess.CommentsDataAccess
import ga.injuk.commentor.adapter.out.persistence.mapper.CommentorMapper
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.out.persistence.*
import ga.injuk.commentor.common.annotation.Adapter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentInteraction
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentsDataAccess: CommentsDataAccess,
    private val commentInteractionsDataAccess: CommentInteractionsDataAccess,

    private val commentorMapper: CommentorMapper,
) : CreateCommentPort, GetCommentPort, ListCommentsPort, ListSubCommentsPort, UpdateCommentPort, DeleteCommentPort,
    BulkDeleteCommentPort, CreateCommentInteractionPort, GetCommentInteractionPort, UpdateCommentInteractionPort,
    DeleteCommentInteractionPort {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun create(user: User, request: CreateCommentRequest): Long {
        val (insertId) = commentsDataAccess.insert(user, request)
        if (insertId == null) {
            throw UncaughtException("failed to create comment resource.")
        }

        return insertId
    }

    override fun get(user: User, request: GetCommentRequest): Comment? {
        val commentRow = commentsDataAccess.findOne(user, request)

        return commentRow?.let { row ->
            commentorMapper.mapToComment(row)
                .getOrElse {
                    logger.error(it.message)
                    throw UncaughtException("failed to convert comment")
                }
        }
    }

    override fun getList(user: User, request: ListCommentsRequest): Pagination<Comment> {
        val (commentRows, cursor) = commentsDataAccess.findBy(user, request)

        val results = commentRows.map { row ->
            commentorMapper.mapToComment(row).getOrElse {
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
            commentorMapper.mapToComment(row).getOrElse {
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
        if (insertId == null) {
            throw UncaughtException("failed to create comment interaction resource.")
        }

        return insertId
    }

    override fun get(user: User, request: GetCommentInteractionRequest): CommentInteraction? {
        val commentInteractionRow = commentInteractionsDataAccess.findOne(user, request)

        return commentInteractionRow?.let {
            CommentInteraction(
                id = it.id ?: throw InvalidArgumentException("interaction id cannot be null"),
                commentId = it.commentId ?: throw InvalidArgumentException("commentId cannot be null"),
                type = CommentInteractionType.from(it.type)
                    ?: throw InvalidArgumentException("interaction type cannot be null"),
                userId = it.userId ?: throw InvalidArgumentException("userId cannot be null"),
            )
        }
    }

    override fun update(user: User, request: UpdateCommentInteractionRequest): Int {
        val affectedRows = commentInteractionsDataAccess.update(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to upsert comment interaction")
    }

    override fun delete(user: User, request: DeleteCommentInteractionRequest): Int {
        val affectedRows = commentInteractionsDataAccess.delete(user, request)

        return affectedRows.count ?: throw UncaughtException("failed to delete comment interaction")
    }
}