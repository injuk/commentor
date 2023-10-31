package ga.injuk.commentor.adapter.core.extension

import ga.injuk.commentor.common.JsonObjectMapper
import ga.injuk.commentor.domain.model.CommentPart
import org.jooq.JSON

internal fun List<CommentPart>?.convertToJooqJson(): JSON {
    val commentParts = this ?: emptyList()
    val mapper = JsonObjectMapper.instance()

    return JSON.valueOf(
        mapper.writeValueAsString(commentParts)
    )
}