package ga.injuk.commentor.domain.model

data class Mark(
    val type: MarkType,
    val attrs: Attrs?,
) {
    data class Attrs(
        val type: MarkAttrsType?,
        val color: String?,
    )
}