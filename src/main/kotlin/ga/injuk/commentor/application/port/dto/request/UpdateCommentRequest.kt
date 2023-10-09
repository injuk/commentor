package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentInteractionType
import ga.injuk.commentor.domain.model.CommentPart

data class UpdateCommentRequest(
    val id: Long,

    val parts: List<CommentPart>? = null,

    val interactions: List<Interaction>? = null,
) {
    data class Interaction(
        val value: Int = 1,
        val isCancelAction: Boolean = false,

        val type: CommentInteractionType,
    )
}