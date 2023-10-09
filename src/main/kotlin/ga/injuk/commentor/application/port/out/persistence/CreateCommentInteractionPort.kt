package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.CreateCommentInteractionRequest
import ga.injuk.commentor.domain.User

interface CreateCommentInteractionPort {
    fun create(user: User, request: CreateCommentInteractionRequest): Long
}