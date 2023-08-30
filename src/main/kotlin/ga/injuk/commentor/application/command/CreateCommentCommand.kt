package ga.injuk.commentor.application.command

import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.response.CreateCommentResponse
import ga.injuk.commentor.application.port.out.persistence.CommentAccessPort
import ga.injuk.commentor.domain.User
import org.springframework.stereotype.Service

@Service
class CreateCommentCommand(
    private val commentAccessPort: CommentAccessPort,
): CreateCommentUseCase {
    override fun execute(user: User, data: CreateCommentRequest?): CreateCommentResponse? {
        val result = commentAccessPort.create(
            user = user,
            request = data ?: throw RuntimeException("데이터 없음")
        )

        // TODO: result를 문자열로 변환할 수 있도록 수정
        return CreateCommentResponse(
            id = "$result temp"
        )
    }
}