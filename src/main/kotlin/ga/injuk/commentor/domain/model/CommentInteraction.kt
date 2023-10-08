package ga.injuk.commentor.domain.model

data class CommentInteraction(
    val id: Long,
    val commentId: Long,
    val type: CommentInteractionType,
    val userId: String,
)