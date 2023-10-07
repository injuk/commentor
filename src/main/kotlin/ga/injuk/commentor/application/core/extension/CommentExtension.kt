package ga.injuk.commentor.application.core.extension

import ga.injuk.commentor.application.port.dto.IdEncodedComment
import ga.injuk.commentor.domain.model.Comment

internal fun Comment.refineWith(id: String)
    = IdEncodedComment(
        id = id,
        isDeleted = this.isDeleted,
        hasSubComments = this.hasSubComments,
        likeCount = this.likeCount,
        dislikeCount = this.dislikeCount,
        created = this.created,
        updated = this.updated,

        parts = if(this.isDeleted) {
            emptyList()
        } else {
            this.parts
        }
    )