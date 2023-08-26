package ga.injuk.commentor.adapter.`in`.rest

import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.models.*
import ga.injuk.commentor.operations.CommentApi
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentorController(
    private val createCommentUseCase: CreateCommentUseCase,
): CommentApi {
    init {
        LoggerFactory
            .getLogger(this.javaClass)
            .debug("createCommentUseCase={}", createCommentUseCase.javaClass)
    }

    override fun createComment(
        authorization: String,
        projectId: String,
        organizationId: String?,
        createCommentRequest: CreateCommentRequest?
    ): ResponseEntity<CreateCommentResponse> {
        // TODO: Request라는 클래스를 정의해서, Request(user, context).use(usecase).run() 식으로 작성해보자
        // authorization, projectId, organizationId로 User를 만듬
        return super.createComment(authorization, projectId, organizationId, createCommentRequest)
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
        return super.listComments(authorization, projectId, limit, nextCursor, organizationId, domain, resourceId)
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