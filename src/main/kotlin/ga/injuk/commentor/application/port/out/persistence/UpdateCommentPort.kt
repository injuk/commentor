package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.domain.User

interface UpdateCommentPort {
    fun update(user: User, request: UpdateCommentRequest): Int
}