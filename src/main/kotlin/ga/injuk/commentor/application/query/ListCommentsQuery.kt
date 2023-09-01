package ga.injuk.commentor.application.query

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.`in`.ListCommentsUseCase
import ga.injuk.commentor.application.port.out.persistence.ListCommentsPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment
import org.springframework.stereotype.Service

@Service
class ListCommentsQuery(
    private val idConverter: IdConverter,
    private val listCommentsPort: ListCommentsPort,
): ListCommentsUseCase {

    override fun execute(user: User, data: ListCommentsRequest?): Pagination<Comment>? {
        // TODO: 반환된 결과의 id는 모두 인코딩해야 함
        val results = listCommentsPort.getList(
            user = user,
            request = data ?: ListCommentsRequest()
        )

        return Pagination(
            results = results,
            nextCursor = null,
        )
    }
}