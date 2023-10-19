package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.domain.model.CommentDomain
import ga.injuk.commentor.domain.model.SortCondition

data class ListCommentsRequest(
    val limit: Long? = 20L,
    val nextCursor: String? = null,
    val resource: Resource? = null,
    val domain: CommentDomain = CommentDomain.NONE,
    val sortCondition: SortCondition = SortCondition(),
)