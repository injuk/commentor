package ga.injuk.commentor.adapter.extension

import com.fasterxml.jackson.databind.ObjectMapper
import ga.injuk.commentor.domain.model.CommentPart
import org.jooq.JSON

internal fun List<CommentPart>?.convertToJooqJson(): JSON {
    val commentParts = this ?: emptyList()

    return JSON.valueOf(
        ObjectMapper().writeValueAsString(commentParts)
    )
}