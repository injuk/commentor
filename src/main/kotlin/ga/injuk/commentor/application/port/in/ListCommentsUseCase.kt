package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.domain.model.Comment

interface ListCommentsUseCase: UseCase<ListCommentsRequest?, Pagination<Comment>?>