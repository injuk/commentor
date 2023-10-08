package ga.injuk.commentor.application.port.dto.request

data class GetCommentInteractionRequest(
    val commentId: Long,
    val withLock: Boolean = false,
)