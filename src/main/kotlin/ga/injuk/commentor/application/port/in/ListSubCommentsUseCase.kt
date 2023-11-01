package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.response.ListCommentsResponse

interface ListSubCommentsUseCase : UseCase<ListCommentsRequest, ListCommentsResponse>