package ga.injuk.commentor.adapter.out.persistence.mapper.impl

import ga.injuk.commentor.adapter.core.exception.InvalidArgumentException
import ga.injuk.commentor.adapter.out.dto.GetCommentResponse
import ga.injuk.commentor.adapter.out.dto.ListCommentsResponse
import ga.injuk.commentor.adapter.out.persistence.mapper.CommentorMapper
import ga.injuk.commentor.domain.model.By
import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.springframework.stereotype.Component

@Component
class CommentorMapperImpl : CommentorMapper {
    override fun mapToComment(target: GetCommentResponse): Result<Comment> = runCatching {
        Comment(
            id = target.id ?: throw InvalidArgumentException("id cannot be null"),
            parts = target.parts ?: throw InvalidArgumentException("parts cannot be null"),
            isDeleted = target.isDeleted ?: throw InvalidArgumentException("isDeleted cannot be null"),
            hasSubComments = target.hasSubComments
                ?: throw InvalidArgumentException("hasSubComments cannot be null"),
            likeCount = target.likeCount ?: throw InvalidArgumentException("likeCount cannot be null"),
            dislikeCount = target.dislikeCount ?: throw InvalidArgumentException("dislikeCount cannot be null"),
            created = Comment.Context(
                at = target.createdAt ?: throw InvalidArgumentException("createdAt cannot be null"),
                by = By(
                    id = target.createdBy ?: throw InvalidArgumentException("createdById cannot be null"),
                ),
            ),
            updated = Comment.Context(
                at = target.updatedAt ?: throw InvalidArgumentException("updatedAt cannot be null"),
                by = By(
                    id = target.updatedBy ?: throw InvalidArgumentException("updatedById cannot be null"),
                ),
            ),
        )
    }

    override fun mapToComment(target: ListCommentsResponse.Row): Result<Comment> = runCatching {
        Comment(
            id = target.id ?: throw InvalidArgumentException("id cannot be null"),
            parts = target.parts ?: throw InvalidArgumentException("parts cannot be null"),
            isDeleted = target.isDeleted ?: throw InvalidArgumentException("isDeleted cannot be null"),
            hasSubComments = target.hasSubComments
                ?: throw InvalidArgumentException("hasSubComments cannot be null"),
            myInteraction = CommentInteractionType.from(target.myInteractionType),
            likeCount = target.likeCount ?: throw InvalidArgumentException("likeCount cannot be null"),
            dislikeCount = target.dislikeCount ?: throw InvalidArgumentException("dislikeCount cannot be null"),
            created = Comment.Context(
                at = target.createdAt ?: throw InvalidArgumentException("createdAt cannot be null"),
                by = By(
                    id = target.createdBy ?: throw InvalidArgumentException("createdById cannot be null"),
                ),
            ),
            updated = Comment.Context(
                at = target.updatedAt ?: throw InvalidArgumentException("updatedAt cannot be null"),
                by = By(
                    id = target.updatedBy ?: throw InvalidArgumentException("updatedById cannot be null"),
                ),
            ),
        )
    }
}