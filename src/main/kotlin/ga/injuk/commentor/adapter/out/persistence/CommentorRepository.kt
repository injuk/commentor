package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.dto.FindByResponseDto
import ga.injuk.commentor.adapter.out.dto.FindOneResponseDto
import ga.injuk.commentor.adapter.out.dto.InsertResponseDto
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.domain.User

interface CommentorRepository {
    fun insert(user: User, request: CreateCommentRequest): InsertResponseDto

    fun findOne(request: GetCommentRequest): FindOneResponseDto?

    fun findBy(user: User, request: ListCommentsRequest): FindByResponseDto

    fun findBy(user: User, request: ListSubCommentsRequest): FindByResponseDto

    fun update(user: User, request: UpdateCommentRequest): AffectedRows

    fun delete(user: User, request: DeleteCommentRequest): AffectedRows

    fun deleteBy(request: BulkDeleteCommentRequest): AffectedRows
}