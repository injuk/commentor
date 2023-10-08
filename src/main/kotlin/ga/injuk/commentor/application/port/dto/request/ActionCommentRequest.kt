package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentInteractionType

data class ActionCommentRequest(
    val id: Long,
    val action: CommentInteractionType,
)