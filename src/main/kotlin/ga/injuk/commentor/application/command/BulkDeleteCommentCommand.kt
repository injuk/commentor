package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.exception.BadRequestException
import ga.injuk.commentor.adapter.exception.InvalidArgumentException
import ga.injuk.commentor.application.port.dto.request.BulkDeleteCommentRequest
import ga.injuk.commentor.application.port.`in`.BulkDeleteCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.BulkDeleteCommentPort
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BulkDeleteCommentCommand(
    private val bulkDeleteCommentPort: BulkDeleteCommentPort,
): BulkDeleteCommentUseCase {
    companion object {
        private const val SYSTEM_USER_ID = "SYSTEM"
    }

    @Transactional
    override fun execute(user: User, data: BulkDeleteCommentRequest) {
        // TODO: 권한을 활용하도록 수정
        if(user.id != SYSTEM_USER_ID) {
            throw BadRequestException("only SYSTEM users can invoke this api.")
        }

        if(data.resourceIds.isEmpty()) {
            throw InvalidArgumentException("resource ids cannot be empty.")
        }

        val affectedRows = bulkDeleteCommentPort.delete(data)
        if(affectedRows == 0) {
            throw BadRequestException("there is no affected comments")
        }
    }
}