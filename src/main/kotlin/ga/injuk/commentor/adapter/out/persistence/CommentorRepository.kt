package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.domain.User

interface CommentorRepository {
    fun insert(user: User, request: CreateCommentRequest): Long?
}