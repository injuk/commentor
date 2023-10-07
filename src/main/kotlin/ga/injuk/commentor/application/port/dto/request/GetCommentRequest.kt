package ga.injuk.commentor.application.port.dto.request

data class GetCommentRequest(
    val commentId: Long,
    val withLock: Boolean = false,
)