package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.UpsertCommentInteractionRequest
import ga.injuk.commentor.domain.User

interface UpsertCommentInteractionPort {
    fun upsert(user: User, request: UpsertCommentInteractionRequest): Int
}