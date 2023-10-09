package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentInteractionType

data class CreateCommentInteractionRequest(
    val commentId: Long,
    val interactionType: CommentInteractionType,
)