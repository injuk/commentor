package ga.injuk.commentor.application.port.`in`.query

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.response.ListCommentsResponse
import ga.injuk.commentor.application.port.`in`.ListCommentsUseCase
import ga.injuk.commentor.application.port.out.persistence.ListCommentsPort
import ga.injuk.commentor.common.CipherHelper
import ga.injuk.commentor.domain.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ListCommentsQuery(
    private val listCommentsPort: ListCommentsPort,
) : ListCommentsUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(ListCommentsQuery::class.java)
    }

    override fun execute(user: User, data: ListCommentsRequest): ListCommentsResponse {
        val decodedCursor = CipherHelper.decode(data.nextCursor)
        val (results, nextCursor) = listCommentsPort.getList(
            user = user,
            request = ListCommentsRequest(
                limit = data.limit,
                nextCursor = decodedCursor,

                resource = data.resource,
                domain = data.domain,
                sortCondition = data.sortCondition,
            ),
        )

        val encoded = CipherHelper.encode(nextCursor)
        logger.info("$nextCursor encoded to $encoded")

        return ListCommentsResponse(
            Pagination(
                results = results.map {
                    it.copy(
                        parts = if (it.isDeleted) {
                            emptyList()
                        } else {
                            it.parts
                        }
                    )
                },
                nextCursor = encoded,
            )
        )
    }
}