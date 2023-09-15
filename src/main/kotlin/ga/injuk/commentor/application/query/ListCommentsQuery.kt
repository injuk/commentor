package ga.injuk.commentor.application.query

import ga.injuk.commentor.application.Base64Helper
import ga.injuk.commentor.application.port.dto.IdEncodedComment
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.response.ListCommentsResponse
import ga.injuk.commentor.application.port.`in`.ListCommentsUseCase
import ga.injuk.commentor.application.port.out.persistence.ListCommentsPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ListCommentsQuery(
    private val idConverter: IdConverter,
    private val listCommentsPort: ListCommentsPort,
): ListCommentsUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(user: User, data: ListCommentsRequest): ListCommentsResponse {
        val (results, nextCursor) = listCommentsPort.getList(
            user = user,
            request = ListCommentsRequest(
                limit = data.limit,
                resource = data.resource,
                domain = data.domain,
                sortConditions = data.sortConditions,

                nextCursor = Base64Helper.decode(data.nextCursor),
            ),
        )

        val encoded = Base64Helper.encode(nextCursor)
        logger.info("nextCursor: $nextCursor")
        logger.info("encoded nextCursor: $encoded")

        return ListCommentsResponse(
            Pagination(
                results = results.map {
                    IdEncodedComment.of(idConverter.encode(it.id), it)
                },
                nextCursor = nextCursor,
            )
        )
    }
}