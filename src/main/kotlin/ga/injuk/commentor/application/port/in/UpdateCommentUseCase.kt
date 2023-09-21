package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.dto.response.UpdateCommentResponse

interface UpdateCommentUseCase: UseCase<UpdateCommentRequest, UpdateCommentResponse>