package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentInteractionType

data class UpdateCommentInteractionRequest(
    val commentId: Long,
    val interactionType: CommentInteractionType,
)