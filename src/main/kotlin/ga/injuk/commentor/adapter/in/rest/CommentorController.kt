package ga.injuk.commentor.adapter.`in`.rest

import ga.injuk.commentor.adapter.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.extension.convert
import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.`in`.ListCommentsUseCase
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.*
import ga.injuk.commentor.models.*
import ga.injuk.commentor.operations.CommentApi
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentorController(
    private val createCommentUseCase: CreateCommentUseCase,
    private val listCommentsUseCase: ListCommentsUseCase,
): CommentApi {
    init {
        LoggerFactory
            .getLogger(this.javaClass)
//            .debug("createCommentUseCase={}", createCommentUseCase.javaClass)
            .debug("listCommentsUseCase={}", listCommentsUseCase.javaClass)
    }

    override fun createComment(
        authorization: String,
        projectId: String,
        organizationId: String?,
        createCommentRequest: CreateCommentRequest?
    ): ResponseEntity<CreateCommentResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()
        val result = createCommentUseCase.execute(
            user = user,
            data = createCommentRequest?.convert() ?: throw InvalidArgumentException("Request has invalid data.")
        )

        return ResponseEntity.ok(
            CreateCommentResponse(result.id)
        )
    }

    override fun listComments(
        authorization: String,
        projectId: String,
        limit: Int?,
        nextCursor: String?,
        organizationId: String?,
        domain: String?,
        resourceId: String?
    ): ResponseEntity<ListCommentsResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()
        val result = listCommentsUseCase.execute(
            user = user,
            data = ListCommentsRequest(
                limit = limit?.toLong(),
                nextCursor = nextCursor,
                domain = when(domain) {
                    "ARTICLE" -> CommentDomain.ARTICLE
                    else -> CommentDomain.NONE
                },
                resource = resourceId?.let { Resource(resourceId) }
            )
        )

        return ResponseEntity.ok(result.convert())
    }

    override fun listSubComments(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        limit: Int?,
        nextCursor: String?
    ): ResponseEntity<ListSubCommentsResponse> {
        return super.listSubComments(id, authorization, projectId, organizationId, limit, nextCursor)
    }

    override fun updateComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        patchCommentRequest: PatchCommentRequest?
    ): ResponseEntity<PatchCommentResponse> {
        return super.updateComment(id, authorization, projectId, organizationId, patchCommentRequest)
    }

    override fun deleteComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?
    ): ResponseEntity<Unit> {
        return super.deleteComment(id, authorization, projectId, organizationId)
    }

    override fun bulkDeleteComments(
        authorization: String,
        projectId: String,
        organizationId: String?,
        bulkDeleteCommentsRequest: BulkDeleteCommentsRequest?
    ): ResponseEntity<Unit> {
        return super.bulkDeleteComments(authorization, projectId, organizationId, bulkDeleteCommentsRequest)
    }

    override fun likeComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?
    ): ResponseEntity<Unit> {
        return super.likeComment(id, authorization, projectId, organizationId)
    }

    override fun dislikeComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?
    ): ResponseEntity<Unit> {
        return super.dislikeComment(id, authorization, projectId, organizationId)
    }
}