package ga.injuk.commentor.domain.model

import ga.injuk.commentor.common.IdConverter
import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val parts: List<CommentPart>,
    val isDeleted: Boolean,
    val hasSubComments: Boolean,
    val likeCount: Long,
    val dislikeCount: Long,
    val created: Context,
    val updated: Context,

    val myInteraction: CommentInteractionType? = null,
) {
    val encodedId: String?
        get() = IdConverter.convert(id)

    data class Context(
        val at: LocalDateTime,
        val by: By,
    )
}