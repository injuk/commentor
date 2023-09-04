package ga.injuk.commentor.adapter.out.persistence.jooq

import com.mzc.cloudplex.download.persistence.jooq.tables.references.COMMENTS
import ga.injuk.commentor.adapter.extension.convertToJooqJson
import ga.injuk.commentor.adapter.out.persistence.CommentorRepository
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.By
import ga.injuk.commentor.domain.model.Comment
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository

@Repository
class PostgreSqlRepository(
    private val dsl: DSLContext,
): CommentorRepository {

    override fun insert(user: User, request: CreateCommentRequest): Long?
        = dsl.transactionResult { trx ->
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

    override fun findBy(user: User, request: ListCommentsRequest): List<Comment> {
        val result = dsl.run {
            val c = COMMENTS.`as`("c")
            val cIdField = c.ID.`as`("id")

            val sc = COMMENTS.`as`("sc")

            val organizationId = user.district.organization?.id

            select(
                cIdField,
                c.ORG_ID,
                c.PROJECT_ID,
                c.DATA,
                c.IS_DELETED,
                `val`(fetchExists(
                    selectOne()
                        .from(sc)
                        .where(sc.ID.eq(cIdField))
                )).`as`("hasSubComments"),
                c.LIKE_COUNT,
                c.DISLIKE_COUNT,
                c.CREATED_AT,
                c.CREATED_BY_ID,
                c.UPDATED_AT,
                c.UPDATED_BY_ID
            ).from(c)
                .where(
                    c.PROJECT_ID.eq(user.district.project.id)
                        .and(
                            organizationId?.let {
                                c.ORG_ID.eq(organizationId)
                            } ?: c.ORG_ID.isNull
                        )
                        .and(c.DOMAIN.eq(request.domain.value))
                        .and(c.RESOURCE_ID.eq(request.resource?.id))
                        .and(c.PARENT_ID.isNull)
                )
                .orderBy(c.CREATED_AT.desc())
                .limit(request.limit)
                .offset(0)
        }.toList()

        return result.map {
            Comment(
                id = it.get(COMMENTS.ID)!!,
                parts = emptyList(),
                isDeleted = it.get(COMMENTS.IS_DELETED)!!,
                hasSubComments = it.get("hasSubComments") as Boolean,
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
        }
    }
}