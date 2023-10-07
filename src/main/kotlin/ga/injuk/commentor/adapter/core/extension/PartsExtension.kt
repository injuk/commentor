package ga.injuk.commentor.adapter.core.extension

import com.fasterxml.jackson.databind.ObjectMapper
import ga.injuk.commentor.application.JsonObjectMapper
import ga.injuk.commentor.domain.model.CommentPart
import org.jooq.JSON

internal fun List<CommentPart>?.convertToJooqJson(): JSON {
    val commentParts = this ?: emptyList()
    val mapper = JsonObjectMapper.instance()

    return JSON.valueOf(
        mapper.writeValueAsString(commentParts)
    )
}