package ga.injuk.commentor.application.port.`in`.command

import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.core.exception.UncaughtException
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.dto.response.CreateCommentResponse
import ga.injuk.commentor.application.port.`in`.CreateSubCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateSubCommentCommand(
    private val getCommentPort: GetCommentPort,
    private val createCommentPort: CreateCommentPort,
) : CreateSubCommentUseCase {

    @Transactional
    override fun execute(user: User, data: CreateCommentRequest): CreateCommentResponse {
        val parentId = data.parentId ?: throw BadRequestException("ParentId is required")
        if (data.parts.isEmpty()) {
            throw BadRequestException("Comment is required")
        }

        val comment = getCommentPort.get(user, GetCommentRequest(commentId = parentId, withLock = true))
            ?: throw ResourceNotFoundException("There is no parent comment")
        if (comment.isDeleted) {
            throw BadRequestException("Cannot create sub-comment for deleted comment")
        }

        val result = createCommentPort.create(
            user = user,
            request = data,
        )

        return CreateCommentResponse(
            id = IdConverter.convert(result) ?: throw UncaughtException("Failed to convert comment id")
        )
    }
}