package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentPart

data class UpdateCommentRequest(
    val id: Long,

    val parts: List<CommentPart>? = null,
    val interactionType: InteractionType? = null,
) {
    enum class InteractionType(val value: String) {
        LIKE("LIKE"),
        CANCEL_LIKE("CANCEL_LIKE"),

        DISLIKE("DISLIKE"),
        CANCEL_DISLIKE("CANCEL_DISLIKE"),
    }
}