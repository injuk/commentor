package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentInteractionType

data class UpsertCommentInteractionRequest(
    val id: Long,
    val interactionType: CommentInteractionType,
)