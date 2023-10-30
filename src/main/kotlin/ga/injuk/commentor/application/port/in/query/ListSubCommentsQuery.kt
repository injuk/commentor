package ga.injuk.commentor.application.port.`in`.query

import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.Base64Helper
import ga.injuk.commentor.application.core.extension.refineWith
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListSubCommentsRequest
import ga.injuk.commentor.application.port.dto.response.ListCommentsResponse
import ga.injuk.commentor.application.port.`in`.ListSubCommentsUseCase
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.application.port.out.persistence.ListSubCommentsPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ListSubCommentsQuery(
    private val idConverter: IdConverter,
    private val getCommentPort: GetCommentPort,
    private val listSubCommentsPort: ListSubCommentsPort,
) : ListSubCommentsUseCase {
    // TODO: base64 이외의 방법으로 nextCursor를 암복호화하기
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(user: User, data: ListSubCommentsRequest): ListCommentsResponse {
        getCommentPort.get(user, GetCommentRequest(commentId = data.parentId))
            ?: throw ResourceNotFoundException("There is no comment")

        val (results, nextCursor) = listSubCommentsPort.getList(
            user = user,
            request = ListSubCommentsRequest(
                parentId = data.parentId,
                limit = data.limit,
                sortCondition = data.sortCondition,
                nextCursor = Base64Helper.decode(data.nextCursor),
            ),
        )
        logger.info("nextCursor: $nextCursor")

        val encoded = Base64Helper.encode(nextCursor)
        logger.info("encoded nextCursor: $encoded")

        return ListCommentsResponse(
            Pagination(
                results = results.map {
                    it.refineWith(idConverter.encode(it.id))
                },
                nextCursor = Base64Helper.encode(nextCursor),
            )
        )
    }
}