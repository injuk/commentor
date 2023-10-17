package ga.injuk.commentor.domain.model

enum class CommentDomain(val value: String) {
    NONE("NONE"),
    ARTICLE("ARTICLE"),
    VIDEO("VIDEO");

    companion object {
        fun from(value: String?): CommentDomain? = values().find { it.name == value }
    }
}