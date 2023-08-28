package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.domain.User

interface CommentAccessPort {
    fun create(user: User, request: CreateCommentRequest): Long?
}