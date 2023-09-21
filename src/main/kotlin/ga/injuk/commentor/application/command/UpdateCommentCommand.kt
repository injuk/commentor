package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.exception.UncaughtException
import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.dto.response.CreateCommentResponse
import ga.injuk.commentor.application.port.dto.response.UpdateCommentResponse
import ga.injuk.commentor.application.port.`in`.UpdateCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.application.port.out.persistence.UpdateCommentPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service

@Service
class UpdateCommentCommand(
    private val idConverter: IdConverter,
    private val updateCommentPort: UpdateCommentPort,
): UpdateCommentUseCase {
    override fun execute(user: User, data: UpdateCommentRequest): UpdateCommentResponse {
        updateCommentPort.update(
            user = user,
            request = data,
        ).run {
            if(equals(0)) {
                throw UncaughtException("failed to update comment")
            }
        }

        return UpdateCommentResponse(
            id = idConverter.encode(data.id)
        )
    }
}