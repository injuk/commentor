package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.adapter.out.dto.*
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.domain.User

interface CommentInteractionsDataAccess {
    fun upsert(user: User, request: UpsertCommentInteractionRequest): AffectedRows

    fun findOne(user: User, request: GetCommentInteractionRequest): GetCommentInteractionResponse?

    fun delete(user: User, request: DeleteCommentInteractionRequest): AffectedRows
}