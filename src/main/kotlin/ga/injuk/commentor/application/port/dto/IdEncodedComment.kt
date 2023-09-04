package ga.injuk.commentor.application.port.dto

import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentPart

data class IdEncodedComment(
    val id: String,
    val parts: List<CommentPart>,
    val isDeleted: Boolean,
    val hasSubComments: Boolean,
    val likeCount: Long,
    val dislikeCount: Long,
    val created: Comment.Context,
    val updated: Comment.Context,
) {
    companion object {
        fun of(id: String, comment: Comment) = IdEncodedComment(
            id = id,
            parts = comment.parts,
            isDeleted = comment.isDeleted,
            hasSubComments = comment.hasSubComments,
            likeCount = comment.likeCount,
            dislikeCount = comment.dislikeCount,
            created = comment.created,
            updated = comment.updated,
        )
    }
}