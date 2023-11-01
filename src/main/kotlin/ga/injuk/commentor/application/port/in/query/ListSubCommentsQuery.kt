package ga.injuk.commentor.application.port.`in`.query

import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.response.ListCommentsResponse
import ga.injuk.commentor.application.port.`in`.ListSubCommentsUseCase
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.application.port.out.persistence.ListCommentsPort
import ga.injuk.commentor.common.CipherHelper
import ga.injuk.commentor.domain.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ListSubCommentsQuery(
    private val getCommentPort: GetCommentPort,
    private val listCommentsPort: ListCommentsPort,
) : ListSubCommentsUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(ListSubCommentsQuery::class.java)
    }

    override fun execute(user: User, data: ListCommentsRequest): ListCommentsResponse {
        if (data.parentId == null) {
            throw BadRequestException("Parent Id cannot be null")
        }
        getCommentPort.get(user, GetCommentRequest(commentId = data.parentId))
            ?: throw ResourceNotFoundException("There is no comment")

        val decodedCursor = CipherHelper.decode(data.nextCursor)
        val (results, nextCursor) = listCommentsPort.getList(
            user = user,
            request = ListCommentsRequest(
                limit = data.limit,
                nextCursor = decodedCursor,

                parentId = data.parentId,
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