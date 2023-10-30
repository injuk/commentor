package ga.injuk.commentor.application.core.strategy

import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType

interface CommentInteractionStrategy {

    fun getInteractions(
        user: User,
        commentId: Long,
        requestedAction: CommentInteractionType,
    ): List<UpdateCommentRequest.Interaction>
}