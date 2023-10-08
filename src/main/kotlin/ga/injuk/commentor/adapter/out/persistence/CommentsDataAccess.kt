package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.dto.ListCommentsResponse
import ga.injuk.commentor.adapter.out.dto.GetCommentResponse
import ga.injuk.commentor.adapter.out.dto.CreateCommentResponse
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.domain.User

interface CommentsDataAccess {
    fun insert(user: User, request: CreateCommentRequest): CreateCommentResponse

    fun findOne(request: GetCommentRequest): GetCommentResponse?

    fun findBy(user: User, request: ListCommentsRequest): ListCommentsResponse

    fun findBy(user: User, request: ListSubCommentsRequest): ListCommentsResponse

    fun update(user: User, request: UpdateCommentRequest): AffectedRows

    fun delete(user: User, request: DeleteCommentRequest): AffectedRows

    fun deleteBy(request: BulkDeleteCommentRequest): AffectedRows
}