package ga.injuk.commentor.application.port.dto.request

import ga.injuk.commentor.domain.model.SortCondition

data class ListSubCommentsRequest(
    val parentId: Long,

    val limit: Long? = 20L,
    val nextCursor: String? = null,
    val sortCondition: SortCondition = SortCondition(),
)