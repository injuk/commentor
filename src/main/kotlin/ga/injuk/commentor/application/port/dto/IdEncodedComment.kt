package ga.injuk.commentor.application.port.dto

import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentInteractionType
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

    val myInteraction: CommentInteractionType? = null,
)