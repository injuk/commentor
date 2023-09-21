package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.exception.UncaughtException
import ga.injuk.commentor.application.port.dto.request.DeleteCommentRequest
import ga.injuk.commentor.application.port.`in`.DeleteCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.DeleteCommentPort
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service

@Service
class DeleteCommentCommand(
    private val deleteCommentPort: DeleteCommentPort,
): DeleteCommentUseCase {
    override fun execute(user: User, data: DeleteCommentRequest) {
        deleteCommentPort.delete(
            user = user,
            request = data,
        ).run {
            if(equals(0)) {
                throw UncaughtException("failed to update comment")
            }
        }
    }
}