package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.exception.BadRequestException
import ga.injuk.commentor.adapter.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.exception.UncaughtException
import ga.injuk.commentor.application.port.dto.request.BulkDeleteCommentRequest
import ga.injuk.commentor.application.port.`in`.BulkDeleteCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.BulkDeleteCommentPort
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service

@Service
class BulkDeleteCommentCommand(
    private val bulkDeleteCommentPort: BulkDeleteCommentPort,
): BulkDeleteCommentUseCase {
    override fun execute(user: User, data: BulkDeleteCommentRequest) {
        if(user.id != "SYSTEM") {
            // TODO: 권한을 활용하도록 수정
            throw BadRequestException("only SYSTEM users can invoke this api.")
        }
        if(data.resourceIds.isEmpty()) {
            throw InvalidArgumentException("resource ids cannot be empty.")
        }
        bulkDeleteCommentPort.delete(data).run {
            if(equals(0)) {
                throw UncaughtException("failed to bulk delete comments.")
            }
        }
    }
}