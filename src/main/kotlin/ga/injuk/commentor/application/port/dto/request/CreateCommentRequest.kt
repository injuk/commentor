package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.domain.model.CommentPart

data class CreateCommentRequest(
    val domain: String?,
    val resource: Resource,
    val parts: List<CommentPart>,

    val parentId: Long? = null,
)