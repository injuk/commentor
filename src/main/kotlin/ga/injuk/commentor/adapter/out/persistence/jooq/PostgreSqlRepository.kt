package ga.injuk.commentor.adapter.out.persistence.jooq

import com.mzc.cloudplex.download.persistence.jooq.tables.references.COMMENTS
import ga.injuk.commentor.adapter.exception.InvalidJsonException
import ga.injuk.commentor.adapter.extension.convertToJooqJson
import ga.injuk.commentor.adapter.out.persistence.CommentorRepository
import ga.injuk.commentor.application.JsonObjectMapper
import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.By
import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentPart
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PostgreSqlRepository(
    private val dsl: DSLContext,
) : CommentorRepository {

    companion object {
        private const val HAS_SUB_COMMENTS = "hasSubComments"
        private const val NEXT_CURSOR = "nextCursor"

        private val c = COMMENTS.`as`("c")
        private val sc = COMMENTS.`as`("sc")

        private fun lpadByZero() = lpad(c.ID.cast(String::class.java), 10, "0")
    }

    override fun insert(user: User, request: CreateCommentRequest): Long? = dsl.transactionResult { trx ->
        trx.dsl()
            .insertInto(COMMENTS)
            .set(COMMENTS.ORG_ID, user.district.organization?.id)
            .set(COMMENTS.PROJECT_ID, user.district.project.id)
            .set(COMMENTS.DOMAIN, request.domain)
            .set(COMMENTS.RESOURCE_ID, request.resource.id)
            .set(COMMENTS.DATA, request.parts.convertToJooqJson())
            .set(COMMENTS.CREATED_BY_ID, user.id)
            .set(COMMENTS.UPDATED_BY_ID, user.id)
            .returningResult(COMMENTS.ID)
    }.single().getValue(COMMENTS.ID)

    override fun findBy(user: User, request: ListCommentsRequest): Pagination<Comment> {
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
                c.LIKE_COUNT,
                c.DISLIKE_COUNT,
                c.CREATED_AT,
                c.CREATED_BY_ID,
                c.UPDATED_AT,
                c.UPDATED_BY_ID
            ).from(c)
                .where(c.PROJECT_ID.eq(user.district.project.id))
                .and(
                    user.district.organization?.id?.let { c.ORG_ID.eq(it) } ?: noCondition()
                )
                .and(c.DOMAIN.eq(request.domain.value))
                .and(c.RESOURCE_ID.eq(request.resource?.id))
                .and(c.PARENT_ID.isNull)

            var sortBy = c.CREATED_AT.desc()
            selectStep.apply {
                if(request.nextCursor == null) {
                    and(noCondition())
                } else {
                    val criteria = when(request.sortConditions.criteria) {
                        ListCommentsRequest.Criteria.UPDATED_AT -> c.UPDATED_AT
                        else -> c.CREATED_AT
                    }

                    and(
                        concat(criteria, lpadByZero())
                            .run {
                                if(request.sortConditions.order == ListCommentsRequest.Order.ASC) {
                                    sortBy = criteria.asc()
                                    lt(request.nextCursor)
                                } else {
                                    gt(request.nextCursor)
                                }
                            }
                    )
                }
            }
                .orderBy(sortBy)
                .limit(limit + 1)
        }.toList()

        // TODO: 디게 꼴베기 싫은데 리팩토링 하자
        var nextCursor: String? = null

        val result = if(response.size > limit) {
            nextCursor = response.last().get(NEXT_CURSOR) as? String
            response.dropLast(1)
        } else {
            response
        }

        return Pagination(
            results = result.map {
                Comment(
                    id = it.get(COMMENTS.ID)!!,
                    parts = convertToComments(it.get(COMMENTS.DATA)),
                    isDeleted = it.get(COMMENTS.IS_DELETED)!!,
                    hasSubComments = it.get(HAS_SUB_COMMENTS) as Boolean,
                    likeCount = it.get(COMMENTS.LIKE_COUNT)!!,
                    dislikeCount = it.get(COMMENTS.DISLIKE_COUNT)!!,
                    created = Comment.Context(
                        at = it.get(COMMENTS.CREATED_AT)!!,
                        by = By(it.get(COMMENTS.CREATED_BY_ID)!!)
                    ),
                    updated = Comment.Context(
                        at = it.get(COMMENTS.UPDATED_AT)!!,
                        by = By(it.get(COMMENTS.UPDATED_BY_ID)!!)
                    ),
                )
            },
            nextCursor = nextCursor,
        )
    }

    override fun update(user: User, request: UpdateCommentRequest): Int = dsl.transactionResult { trx ->
        val comment = trx.dsl()
            .select(
                COMMENTS.UPDATED_BY_ID
            )
            .from(COMMENTS)
            .where(COMMENTS.ID.eq(request.id))
            .singleOrNull() ?: throw RuntimeException("there is no comment exists.")

        if(comment.getValue(COMMENTS.UPDATED_BY_ID) != user.id) {
            throw RuntimeException("cannot update other's comment.")
        }

        trx.dsl()
            .update(COMMENTS)
            .set(COMMENTS.UPDATED_AT, LocalDateTime.now())
            .set(COMMENTS.DATA, request.parts.convertToJooqJson())
            .where(COMMENTS.ID.eq(request.id))
            .execute()
    }

    private fun convertToComments(jooqJson: JSON?): List<CommentPart> = jooqJson?.let { json ->
        val mapper = JsonObjectMapper.instance()
        mapper.readValue(json.data().toByteArray(), List::class.java).map {
            mapper.convertValue(it, CommentPart::class.java)
        }
    } ?: throw InvalidJsonException("Comment cannot be null.")
}