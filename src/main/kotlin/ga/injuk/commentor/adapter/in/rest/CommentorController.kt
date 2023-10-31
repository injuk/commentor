package ga.injuk.commentor.adapter.`in`.rest

import ga.injuk.commentor.adapter.core.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.core.exception.UncaughtException
import ga.injuk.commentor.adapter.core.extension.convert
import ga.injuk.commentor.adapter.core.extension.convertWith
import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.BulkDeleteCommentRequest
import ga.injuk.commentor.application.port.dto.request.DeleteCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.`in`.*
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentDomain
import ga.injuk.commentor.domain.model.CommentInteractionType
import ga.injuk.commentor.models.*
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
    private val createSubComment: CreateSubCommentUseCase,
    private val bulkDeleteComments: BulkDeleteCommentUseCase,
    private val actionComment: ActionCommentUseCase,
    private val idConverter: IdConverter,
) : CommentApi {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun createComment(
        authorization: String,
        projectId: String,
        organizationId: String?,
        createCommentRequest: CreateCommentRequest?,
    ): ResponseEntity<CreateCommentResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val data = createCommentRequest.tryRemoveNullability()
            .convert()
        val (id) = createComment.execute(user, data)

        return ResponseEntity.ok(
            CreateCommentResponse(id)
        )
    }

    override fun listComments(
        authorization: String,
        projectId: String,
        limit: Int?,
        nextCursor: String?,
        organizationId: String?,
        domain: String?,
        resourceId: String?,
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
                domain = when (domain) {
                    "ARTICLE" -> CommentDomain.ARTICLE
                    else -> CommentDomain.NONE
                },
                resource = resourceId?.let { Resource(resourceId) }
            )
        )

        return ResponseEntity.ok(
            result.convert()
        )
    }

    override fun listSubComments(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        limit: Int?,
        nextCursor: String?,
    ): ResponseEntity<ListSubCommentsResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val (results, cursor) = listSubComments.execute(
            user = user,
            data = ListCommentsRequest(
                limit = limit?.toLong(),
                nextCursor = nextCursor,
                parentId = id.decodeToLong()
            )
        ).convert()

        return ResponseEntity.ok(
            ListSubCommentsResponse(results, cursor)
        )
    }

    override fun updateComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        patchCommentRequest: PatchCommentRequest?,
    ): ResponseEntity<PatchCommentResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val request = patchCommentRequest.tryRemoveNullability()
        val (id) = updateComment.execute(
            user = user,
            data = UpdateCommentRequest(
                id = id.decodeToLong(),
                parts = request.parts.map { it.convert() }
            )
        )

        return ResponseEntity.ok(
            PatchCommentResponse(id)
        )
    }

    override fun deleteComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
    ): ResponseEntity<Unit> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        deleteComment.execute(
            user = user,
            data = DeleteCommentRequest(id.decodeToLong())
        )

        return responseWithNoContent()
    }

    override fun actionComments(
        authorization: String,
        projectId: String,
        organizationId: String?,
        bulkDeleteCommentsRequest: BulkDeleteCommentsRequest?,
    ): ResponseEntity<Unit> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val request = bulkDeleteCommentsRequest.tryRemoveNullability()
        val resourceDomain = CommentDomain.from(request.data.domain)
            ?: throw InvalidArgumentException("Request has invalid resource domain.")

        bulkDeleteComments.execute(
            user = user,
            data = BulkDeleteCommentRequest(
                resourceIds = request.data.resource.ids,
                domain = resourceDomain,
            )
        )

        return responseWithNoContent()
    }

    override fun actionComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        actionCommentRequest: ActionCommentRequest?,
    ): ResponseEntity<Unit> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val request = actionCommentRequest.tryRemoveNullability()
        actionComment.execute(
            user = user,
            data = ga.injuk.commentor.application.port.dto.request.ActionCommentRequest(
                id = id.decodeToLong(),
                action = when (request.type) {
                    ActionCommentRequest.Type.LIKE -> CommentInteractionType.LIKE
                    ActionCommentRequest.Type.DISLIKE -> CommentInteractionType.DISLIKE
                }
            )
        )

        return responseWithNoContent()
    }

    override fun createSubComment(
        id: String,
        authorization: String,
        projectId: String,
        organizationId: String?,
        createSubCommentRequest: CreateSubCommentRequest?,
    ): ResponseEntity<CreateSubCommentResponse> {
        val user = User.builder()
            .setAuthorization(authorization)
            .setProject(projectId)
            .setOrganization(organizationId)
            .build()

        val parentId = id.decodeToLong()
        val data = createSubCommentRequest.tryRemoveNullability()
            .convertWith(parentId)
        val result = createSubComment.execute(user, data)

        return ResponseEntity.ok(
            CreateSubCommentResponse(result.id)
        )
    }

    private fun <T> T?.tryRemoveNullability() = this ?: throw UncaughtException("Request data cannot be null")

    private fun String.decodeToLong() = idConverter.decode(this) ?: throw InvalidArgumentException("Cannot decode id")

    private fun <T> responseWithNoContent() = ResponseEntity.noContent().build<T>()
}