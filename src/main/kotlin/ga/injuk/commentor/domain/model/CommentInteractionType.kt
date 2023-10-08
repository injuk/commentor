package ga.injuk.commentor.domain.model

enum class CommentInteractionType(val value: String) {
    LIKE("LIKE"),
    DISLIKE("DISLIKE");

    companion object {
        fun from(type: String?) = values().find { it.value == type }
    }
}