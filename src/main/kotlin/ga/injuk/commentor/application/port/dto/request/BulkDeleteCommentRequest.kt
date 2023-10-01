package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.CommentDomain

data class BulkDeleteCommentRequest(
    val resourceIds: List<String>,
    val domain: CommentDomain,
)