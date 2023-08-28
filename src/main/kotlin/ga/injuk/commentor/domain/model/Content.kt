package ga.injuk.commentor.domain.model

data class Content(
    val type: ContentType,
    val text: String,
    val marks: List<Mark>?,
    val attrs: Attrs?,
) {
    data class Attrs(
        val id: String?,
        val text: String?,
    )
}