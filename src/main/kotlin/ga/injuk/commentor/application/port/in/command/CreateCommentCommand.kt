package ga.injuk.commentor.application.port.`in`.command

import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.response.CreateCommentResponse
import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateCommentCommand(
    private val idConverter: IdConverter,
    private val createCommentPort: CreateCommentPort,
) : CreateCommentUseCase {

    @Transactional
    override fun execute(user: User, data: CreateCommentRequest): CreateCommentResponse {
        if (data.parts.isEmpty()) {
            throw BadRequestException("Comment is required")
        }

        val result = createCommentPort.create(
            user = user,
            request = data,
        )

        return CreateCommentResponse(
            id = idConverter.encode(result)
        )
    }
}