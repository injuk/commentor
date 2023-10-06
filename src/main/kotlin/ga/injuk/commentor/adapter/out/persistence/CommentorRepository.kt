package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment

interface CommentorRepository {
    fun insert(user: User, request: CreateCommentRequest): Long?

    fun findOne(request: GetCommentRequest): Comment?

    fun findBy(user: User, request: ListCommentsRequest): Pagination<Comment>

    fun findBy(user: User, request: ListSubCommentsRequest): Pagination<Comment>

    fun update(user: User, request: UpdateCommentRequest): Int

    fun delete(user: User, request: DeleteCommentRequest): Int

    fun deleteBy(request: BulkDeleteCommentRequest): Int
}