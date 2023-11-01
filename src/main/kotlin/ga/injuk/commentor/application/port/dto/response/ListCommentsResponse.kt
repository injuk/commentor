package ga.injuk.commentor.application.port.dto.response

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.domain.model.Comment

data class ListCommentsResponse(
    val data: Pagination<Comment>,
)