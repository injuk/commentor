package ga.injuk.commentor.adapter.`in`.rest

import ga.injuk.commentor.adapter.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.extension.convert
import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.`in`.*
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.*
import ga.injuk.commentor.models.*
import ga.injuk.commentor.models.CreateCommentRequest
import ga.injuk.commentor.operations.CommentApi
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentorController(
    private val createComment: CreateCommentUseCase,
    private val listComments: ListCommentsUseCase,
    private val listSubComments: ListSubCommentsUseCase,
    private val updateComment: UpdateCommentUseCase,
    private val deleteComment: DeleteCommentUseCase,
    private val bulkDeleteComments: BulkDeleteCommentUseCase,
    private val idConverter: IdConverter,
): CommentApi {
    private val logger = LoggerFactory.getLogger(this.javaClass)

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
        val result = createComment.execute(
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
        val result = listComments.execute(
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
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val (results, nextCursor) = listSubComments.execute(
            user = user,
            data = ListSubCommentsRequest(
                limit = limit?.toLong(),
                nextCursor = nextCursor,
                parentId = idConverter.decode(id) ?: throw InvalidArgumentException("Request has invalid data."),
            )
        ).convert()

        return ResponseEntity.ok(
            ListSubCommentsResponse(results, nextCursor)
        )
    }

    override fun updateComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        patchCommentRequest: PatchCommentRequest?
    ): ResponseEntity<PatchCommentResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()
        val result = updateComment.execute(
            user = user,
            data = UpdateCommentRequest(
                id = idConverter.decode(id) ?: throw InvalidArgumentException("Request has invalid data."),
                parts = patchCommentRequest?.parts?.map { it.convert() } ?: throw InvalidArgumentException("Request has invalid data.")
            )
        )

        return ResponseEntity.ok(
            PatchCommentResponse(result.id)
        )
    }

    override fun deleteComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?
    ): ResponseEntity<Unit> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        deleteComment.execute(
            user = user,
            data = DeleteCommentRequest(
                id = idConverter.decode(id) ?: throw InvalidArgumentException("Request has invalid data."),
            )
        )

        return ResponseEntity.noContent().build()
    }

    override fun bulkDeleteComments(
        authorization: String,
        projectId: String,
        organizationId: String?,
        bulkDeleteCommentsRequest: BulkDeleteCommentsRequest?
    ): ResponseEntity<Unit> {
        if(bulkDeleteCommentsRequest == null) {
            throw InvalidArgumentException("Request cannot be null.")
        }

        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val resourceDomain = CommentDomain.from(bulkDeleteCommentsRequest.domain) ?: throw InvalidArgumentException("Request has invalid resource domain.")

        bulkDeleteComments.execute(
            user = user,
            data = BulkDeleteCommentRequest(
                resourceIds = bulkDeleteCommentsRequest.resource.ids,
                domain = resourceDomain,
            )
        )

        return ResponseEntity.noContent().build()
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