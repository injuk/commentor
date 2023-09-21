package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment

interface CommentorRepository {
    fun insert(user: User, request: CreateCommentRequest): Long?

    fun findBy(user: User, request: ListCommentsRequest): Pagination<Comment>

    fun update(user: User, request: UpdateCommentRequest): Int
}