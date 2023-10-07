package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.core.exception.BadRequestException
import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.DeleteCommentRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.`in`.DeleteCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.DeleteCommentPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteCommentCommand(
    private val getCommentPort: GetCommentPort,
    private val deleteCommentPort: DeleteCommentPort,
): DeleteCommentUseCase {

    @Transactional
    override fun execute(user: User, data: DeleteCommentRequest) {
        val comment = getCommentPort.get(user, GetCommentRequest(commentId = data.id, withLock = true))
            ?: throw ResourceNotFoundException("there is no comment")

        if(user.id != comment.created.by.id) {
            throw BadRequestException("cannot delete other's comment")
        }
        if(comment.isDeleted) {
            throw BadRequestException("comment has already been deleted")
        }

        val affectedRows = deleteCommentPort.delete(user, data)
        if(affectedRows == 0) {
            throw BadRequestException("there is no comment deleted")
        }
    }
}