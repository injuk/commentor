package ga.injuk.commentor.adapter.out.persistence.mapper

import ga.injuk.commentor.adapter.out.dto.GetCommentResponse
import ga.injuk.commentor.adapter.out.dto.ListCommentsResponse
import ga.injuk.commentor.domain.model.Comment

interface CommentorMapper {
    fun mapToComment(target: GetCommentResponse): Result<Comment>

    fun mapToComment(target: ListCommentsResponse.Row): Result<Comment>
}