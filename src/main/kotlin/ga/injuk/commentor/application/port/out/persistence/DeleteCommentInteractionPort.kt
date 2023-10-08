package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.DeleteCommentInteractionRequest
import ga.injuk.commentor.domain.User

interface DeleteCommentInteractionPort {
    fun delete(user: User, request: DeleteCommentInteractionRequest): Int
}