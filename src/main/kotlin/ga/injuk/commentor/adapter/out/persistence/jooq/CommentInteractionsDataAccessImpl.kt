package ga.injuk.commentor.adapter.out.persistence.jooq

import com.mzc.cloudplex.download.persistence.jooq.tables.references.COMMENT_INTERACTIONS
import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.dto.GetCommentInteractionResponse
import ga.injuk.commentor.adapter.out.persistence.CommentInteractionsDataAccess
import ga.injuk.commentor.application.port.dto.request.DeleteCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.UpsertCommentInteractionRequest
import ga.injuk.commentor.domain.User
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CommentInteractionsDataAccessImpl(
    private val dsl: DSLContext,
): CommentInteractionsDataAccess {
    override fun upsert(user: User, request: UpsertCommentInteractionRequest): AffectedRows {
        val response = dsl.run {
            insertInto(COMMENT_INTERACTIONS)
                .set(COMMENT_INTERACTIONS.COMMENT_ID, request.commentId)
                .set(COMMENT_INTERACTIONS.TYPE, request.interactionType.value)
                .set(COMMENT_INTERACTIONS.USER_ID, user.id)
                .onDuplicateKeyUpdate()
                .set(COMMENT_INTERACTIONS.COMMENT_ID, request.commentId)
                .set(COMMENT_INTERACTIONS.TYPE, request.interactionType.value)
                .set(COMMENT_INTERACTIONS.USER_ID, user.id)
                .execute()
        }

        return AffectedRows(response)
    }

    override fun findOne(user: User, request: GetCommentInteractionRequest): GetCommentInteractionResponse? {
        val response = dsl.run {
            select(
                COMMENT_INTERACTIONS.ID,
                COMMENT_INTERACTIONS.COMMENT_ID,
                COMMENT_INTERACTIONS.TYPE,
                COMMENT_INTERACTIONS.USER_ID,
            )
                .from(COMMENT_INTERACTIONS)
                .where(COMMENT_INTERACTIONS.USER_ID.eq(user.id))
                .and(COMMENT_INTERACTIONS.COMMENT_ID.eq(request.commentId))
                .apply { if(request.withLock) forUpdate() }
        }.singleOrNull()

        return response?.let {
            GetCommentInteractionResponse(
                id = it.get(COMMENT_INTERACTIONS.ID),
                commentId = it.get(COMMENT_INTERACTIONS.COMMENT_ID),
                type = it.get(COMMENT_INTERACTIONS.TYPE),
                userId = it.get(COMMENT_INTERACTIONS.USER_ID),
            )
        }
    }

    override fun delete(user: User, request: DeleteCommentInteractionRequest): AffectedRows {
        val response = dsl.run {
            deleteFrom(COMMENT_INTERACTIONS)
                .where(COMMENT_INTERACTIONS.USER_ID.eq(user.id))
                .and(COMMENT_INTERACTIONS.COMMENT_ID.eq(request.commentId))
                .execute()
        }

        return AffectedRows(response)
    }
}