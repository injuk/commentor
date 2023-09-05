package ga.injuk.commentor.application.command

import ga.injuk.commentor.application.exception.UncaughtException
import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.response.CreateCommentResponse
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service

@Service
class CreateCommentCommand(
    private val idConverter: IdConverter,
    private val createCommentPort: CreateCommentPort,
): CreateCommentUseCase {
    override fun execute(user: User, data: CreateCommentRequest): CreateCommentResponse {
        val result = createCommentPort.create(
            user = user,
            request = data,
        ) ?: throw UncaughtException("Failed to create comment resource.")

        return CreateCommentResponse(
            id = idConverter.encode(result)
        )
    }
}