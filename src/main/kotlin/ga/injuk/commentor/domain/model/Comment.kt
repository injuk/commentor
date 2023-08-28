package ga.injuk.commentor.domain.model

import java.time.LocalDateTime

data class Comment(
    val id: String,
    val parts: List<CommentPart>,
    val isDeleted: Boolean,
    val hasSubComments: Boolean,
    val likeCount: Int,
    val dislikeCount: Int,
    val created: Context,
    val updated: Context,
) {
    data class Context(
        val at: LocalDateTime,
        val by: By,
    )
}