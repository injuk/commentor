package ga.injuk.commentor.adapter.out.dto

import ga.injuk.commentor.domain.model.CommentPart
import java.time.LocalDateTime

data class GetCommentResponse(
    val id: Long?,
    val parts: List<CommentPart>?,
    val isDeleted: Boolean?,
    val hasSubComments: Boolean?,
    val myInteractionType: String?,
    val likeCount: Long?,
    val dislikeCount: Long?,
    val createdAt: LocalDateTime?,
    val createdBy: String?,
    val updatedAt: LocalDateTime?,
    val updatedBy: String?,
)