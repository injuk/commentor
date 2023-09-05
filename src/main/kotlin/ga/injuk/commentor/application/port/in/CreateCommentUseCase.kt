package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.response.CreateCommentResponse

interface CreateCommentUseCase: UseCase<CreateCommentRequest, CreateCommentResponse>