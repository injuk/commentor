package ga.injuk.commentor.application.port.dto.response

import ga.injuk.commentor.application.port.dto.IdEncodedComment
import ga.injuk.commentor.application.port.dto.Pagination

data class ListCommentsResponse(
    val data: Pagination<IdEncodedComment>
)