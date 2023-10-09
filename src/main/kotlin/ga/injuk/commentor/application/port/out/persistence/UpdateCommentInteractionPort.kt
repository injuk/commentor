package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.UpdateCommentInteractionRequest
import ga.injuk.commentor.domain.User

interface UpdateCommentInteractionPort {
    fun update(user: User, request: UpdateCommentInteractionRequest): Int
}