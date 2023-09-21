package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.DeleteCommentRequest
import ga.injuk.commentor.domain.User

interface DeleteCommentPort {
    fun delete(user: User, request: DeleteCommentRequest): Int
}