package ga.injuk.commentor.domain.model

data class CommentPart(
    val type: CommentPartType,
    val attrs: Attrs?,
    val content: List<Content>,
) {
    data class Attrs(
        val level: Int?,
    )
}