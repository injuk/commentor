package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentPart

data class UpdateCommentRequest(
    val id: Long,
    val parts: List<CommentPart>,
)