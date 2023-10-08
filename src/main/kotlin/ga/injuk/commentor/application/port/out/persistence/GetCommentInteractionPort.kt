package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.GetCommentInteractionRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteraction

interface GetCommentInteractionPort {
    fun get(user: User, request: GetCommentInteractionRequest): CommentInteraction?
}