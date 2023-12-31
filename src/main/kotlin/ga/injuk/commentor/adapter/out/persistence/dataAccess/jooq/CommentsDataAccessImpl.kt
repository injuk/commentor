package ga.injuk.commentor.adapter.out.persistence.dataAccess.jooq

import com.mzc.cloudplex.download.persistence.jooq.tables.references.COMMENTS
import com.mzc.cloudplex.download.persistence.jooq.tables.references.COMMENT_INTERACTIONS
import ga.injuk.commentor.adapter.core.extension.convertToJooqJson
import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.dto.CreateCommentResponse
import ga.injuk.commentor.adapter.out.dto.GetCommentResponse
import ga.injuk.commentor.adapter.out.dto.ListCommentsResponse
import ga.injuk.commentor.adapter.out.persistence.dataAccess.CommentsDataAccess
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.common.JsonObjectMapper
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType.DISLIKE
import ga.injuk.commentor.domain.model.CommentInteractionType.LIKE
import ga.injuk.commentor.domain.model.CommentPart
import ga.injuk.commentor.domain.model.SortCondition
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CommentsDataAccessImpl(
    private val dsl: DSLContext,
) : CommentsDataAccess {
    companion object {
        private const val HAS_SUB_COMMENTS = "hasSubComments"
        private const val NEXT_CURSOR = "nextCursor"

        private val c = COMMENTS.`as`("c")
        private val sc = COMMENTS.`as`("sc")

        private fun lpadByZero() = lpad(c.ID.cast(String::class.java), 10, "0")
    }

    override fun insert(user: User, request: CreateCommentRequest): CreateCommentResponse {
        val response = dsl.run {
            insertInto(COMMENTS)
                .set(COMMENTS.ORG_ID, user.district.organization?.id)
                .set(COMMENTS.PROJECT_ID, user.district.project.id)
                .set(COMMENTS.DOMAIN, request.domain)
                .set(COMMENTS.RESOURCE_ID, request.resource.id)
                .set(COMMENTS.DATA, request.parts.convertToJooqJson())
                .set(COMMENTS.PARENT_ID, request.parentId)
                .set(COMMENTS.CREATED_BY_ID, user.id)
                .set(COMMENTS.UPDATED_BY_ID, user.id)
                .returningResult(COMMENTS.ID)
        }.singleOrNull()?.getValue(COMMENTS.ID)

        return CreateCommentResponse(response)
    }

    override fun findOne(user: User, request: GetCommentRequest): GetCommentResponse? {
        val response = dsl.run {
            select(
                c.ID,
                c.ORG_ID,
                c.PROJECT_ID,
                c.DATA,
                c.IS_DELETED,
                field(
                    exists(
                        selectOne()
                            .from(sc)
                            .where(
                                sc.PARENT_ID.eq(c.ID)
                            )
                    )
                ).`as`(HAS_SUB_COMMENTS),
                c.LIKE_COUNT,
                c.DISLIKE_COUNT,
                c.CREATED_AT,
                c.CREATED_BY_ID,
                c.UPDATED_AT,
                c.UPDATED_BY_ID
            )
                .from(c)
                .where(c.ID.eq(request.commentId))
                .apply { if (request.withLock) forUpdate() }
        }.singleOrNull()

        return response?.let {
            GetCommentResponse(
                id = it.get(COMMENTS.ID),
                parts = convertToComments(it.get(COMMENTS.DATA)),
                isDeleted = it.get(COMMENTS.IS_DELETED),
                hasSubComments = it.get(HAS_SUB_COMMENTS) as? Boolean,
                likeCount = it.get(COMMENTS.LIKE_COUNT),
                dislikeCount = it.get(COMMENTS.DISLIKE_COUNT),
                createdAt = it.get(COMMENTS.CREATED_AT),
                createdBy = it.get(COMMENTS.CREATED_BY_ID),
                updatedAt = it.get(COMMENTS.UPDATED_AT),
                updatedBy = it.get(COMMENTS.UPDATED_BY_ID),
            )
        }
    }

    override fun findBy(user: User, request: ListCommentsRequest): ListCommentsResponse {
        val limit = request.limit ?: 20L

        val response = dsl.run {
            val selectStep = select(
                c.ID,
                c.ORG_ID,
                c.PROJECT_ID,
                c.DATA,
                c.IS_DELETED,
                field(
                    exists(
                        selectOne()
                            .from(sc)
                            .where(
                                sc.PARENT_ID.eq(c.ID)
                            )
                    )
                ).`as`(HAS_SUB_COMMENTS),
                concat(
                    c.CREATED_AT,
                    lpadByZero(),
                ).`as`(NEXT_CURSOR),
                COMMENT_INTERACTIONS.TYPE,
                c.LIKE_COUNT,
                c.DISLIKE_COUNT,
                c.CREATED_AT,
                c.CREATED_BY_ID,
                c.UPDATED_AT,
                c.UPDATED_BY_ID
            ).from(c)
                .leftJoin(COMMENT_INTERACTIONS)
                .on(
                    COMMENT_INTERACTIONS.COMMENT_ID.eq(c.ID)
                        .and(COMMENT_INTERACTIONS.USER_ID.eq(user.id))
                )
                .where(c.PROJECT_ID.eq(user.district.project.id))
                .and(
                    user.district.organization?.id?.let { c.ORG_ID.eq(it) } ?: noCondition()
                )
                .apply {
                    if (request.parentId == null) {
                        and(c.DOMAIN.eq(request.domain.value))
                        and(c.RESOURCE_ID.eq(request.resource?.id))
                        and(c.PARENT_ID.isNull)
                    } else {
                        // 부모 댓글에 대한 자식 댓글 검색
                        and(c.PARENT_ID.eq(request.parentId))
                    }
                }

            val (criteria, order) = request.sortCondition.decideCriteriaAndOrder()

            selectStep.apply {
                if (request.nextCursor != null) {
                    and(
                        concat(criteria, lpadByZero()).run {
                            when (request.sortCondition.order) {
                                SortCondition.Order.ASC -> ge(request.nextCursor)
                                SortCondition.Order.DESC -> le(request.nextCursor)
                            }
                        }
                    )
                }
            }
                .orderBy(order)
                .limit(limit + 1)
        }.toList()

        val (result, cursorCandidate) = response.getCursorCandidateOrNullBy(limit)

        return ListCommentsResponse(
            rows = result.map {
                ListCommentsResponse.Row(
                    id = it.get(COMMENTS.ID),
                    parts = convertToComments(it.get(COMMENTS.DATA)),
                    isDeleted = it.get(COMMENTS.IS_DELETED),
                    hasSubComments = it.get(HAS_SUB_COMMENTS) as? Boolean,
                    myInteractionType = it.get(COMMENT_INTERACTIONS.TYPE),
                    likeCount = it.get(COMMENTS.LIKE_COUNT),
                    dislikeCount = it.get(COMMENTS.DISLIKE_COUNT),
                    createdAt = it.get(COMMENTS.CREATED_AT),
                    createdBy = it.get(COMMENTS.CREATED_BY_ID),
                    updatedAt = it.get(COMMENTS.UPDATED_AT),
                    updatedBy = it.get(COMMENTS.UPDATED_BY_ID),
                )
            },
            cursor = cursorCandidate?.get(NEXT_CURSOR) as? String,
        )
    }

    private fun <T> List<T>.getCursorCandidateOrNullBy(limit: Long) = if (this.size > limit) {
        this.dropLast(1) to this.last()
    } else {
        this to null
    }

    private fun SortCondition.decideCriteriaAndOrder() = this.run {
        val criteria = when (this.criteria) {
            SortCondition.Criteria.CREATED_AT -> c.CREATED_AT
            SortCondition.Criteria.UPDATED_AT -> c.UPDATED_AT
        }

        val order = when (this.order) {
            SortCondition.Order.ASC -> listOf(criteria.asc(), c.ID.asc())
            SortCondition.Order.DESC -> listOf(criteria.desc(), c.ID.desc())
        }

        criteria to order
    }

    override fun update(user: User, request: UpdateCommentRequest): AffectedRows = dsl.run {
        val response = update(COMMENTS)
            .set(COMMENTS.UPDATED_BY_ID, user.id)
            .set(COMMENTS.UPDATED_AT, LocalDateTime.now())
            .apply {
                request.parts?.let {
                    set(COMMENTS.DATA, it.convertToJooqJson())
                }
                request.interactions?.let { interaction ->
                    interaction.forEach {
                        val (value, isCancelAction, type) = it
                        when (type) {
                            LIKE -> set(
                                COMMENTS.LIKE_COUNT,
                                if (isCancelAction) COMMENTS.LIKE_COUNT.minus(value) else COMMENTS.LIKE_COUNT.plus(value)
                            )
                            DISLIKE -> set(
                                COMMENTS.DISLIKE_COUNT,
                                if (isCancelAction) COMMENTS.DISLIKE_COUNT.minus(value) else COMMENTS.DISLIKE_COUNT.plus(
                                    value)
                            )
                        }
                    }
                }
            }
            .where(COMMENTS.ID.eq(request.id))
            .execute()

        return AffectedRows(response)
    }

    override fun delete(user: User, request: DeleteCommentRequest): AffectedRows = dsl.run {
        val response = update(COMMENTS)
            .set(COMMENTS.UPDATED_BY_ID, user.id)
            .set(COMMENTS.UPDATED_AT, LocalDateTime.now())
            .set(COMMENTS.IS_DELETED, true)
            .where(COMMENTS.ID.eq(request.id))
            .execute()

        return AffectedRows(response)
    }

    override fun deleteBy(request: BulkDeleteCommentRequest): AffectedRows = dsl.run {
        val response = deleteFrom(COMMENTS)
            .where(COMMENTS.RESOURCE_ID.`in`(request.resourceIds))
            .and(COMMENTS.DOMAIN.eq(request.domain.value))
            .execute()

        return AffectedRows(response)
    }

    private fun convertToComments(jooqJson: JSON?): List<CommentPart>? = jooqJson?.let { json ->
        val mapper = JsonObjectMapper.instance()
        mapper.readValue(json.data().toByteArray(), List::class.java).map {
            mapper.convertValue(it, CommentPart::class.java)
        }
    }
}