package ga.injuk.commentor.adapter.out.persistence.jooq

import com.mzc.cloudplex.download.persistence.jooq.tables.references.COMMENTS
import ga.injuk.commentor.adapter.extension.convertToJooqJson
import ga.injuk.commentor.adapter.out.persistence.CommentorRepository
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.domain.User
import org.jooq.DSLContext
import org.jooq.JSON
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
}