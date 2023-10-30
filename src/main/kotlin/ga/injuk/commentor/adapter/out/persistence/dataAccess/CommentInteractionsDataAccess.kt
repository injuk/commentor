package ga.injuk.commentor.adapter.out.persistence.dataAccess

import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.dto.CreateCommentInteractionResponse
import ga.injuk.commentor.adapter.out.dto.GetCommentInteractionResponse
import ga.injuk.commentor.application.port.dto.request.CreateCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.DeleteCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentInteractionRequest
import ga.injuk.commentor.domain.User

interface CommentInteractionsDataAccess {
    fun insert(user: User, request: CreateCommentInteractionRequest): CreateCommentInteractionResponse

    fun update(user: User, request: UpdateCommentInteractionRequest): AffectedRows

    fun findOne(user: User, request: GetCommentInteractionRequest): GetCommentInteractionResponse?

    fun delete(user: User, request: DeleteCommentInteractionRequest): AffectedRows
}