package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.core.exception.BadRequestException
import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.dto.response.UpdateCommentResponse
import ga.injuk.commentor.application.port.`in`.UpdateCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.application.port.out.persistence.UpdateCommentPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCommentCommand(
    private val idConverter: IdConverter,
    private val getCommentPort: GetCommentPort,
    private val updateCommentPort: UpdateCommentPort,
): UpdateCommentUseCase {

    @Transactional
    override fun execute(user: User, data: UpdateCommentRequest): UpdateCommentResponse {
        val comment = getCommentPort.get(user, GetCommentRequest(commentId = data.id, withLock = true))
            ?: throw ResourceNotFoundException("there is no comment")

        if(user.id != comment.created.by.id) {
            throw BadRequestException("cannot update other's comment")
        }
        if(comment.isDeleted) {
            throw BadRequestException("comment has already been deleted")
        }

        val affectedRows = updateCommentPort.update(user, data)
        if(affectedRows == 0) {
            throw BadRequestException("there is no comment updated")
        }

        return UpdateCommentResponse(
            id = idConverter.encode(data.id)
        )
    }
}