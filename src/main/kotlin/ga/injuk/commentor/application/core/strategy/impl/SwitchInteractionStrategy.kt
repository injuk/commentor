package ga.injuk.commentor.application.core.strategy.impl

import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.application.port.dto.request.UpdateCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.out.persistence.UpdateCommentInteractionPort
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.springframework.stereotype.Component

@Component
class SwitchInteractionStrategy(
    private val updateCommentInteractionPort: UpdateCommentInteractionPort,
) : CommentInteractionStrategy {

    override fun getInteractions(
        user: User,
        commentId: Long,
        requestedAction: CommentInteractionType,
    ): List<UpdateCommentRequest.Interaction> {
        updateCommentInteractionPort.update(user, UpdateCommentInteractionRequest(
            commentId = commentId,
            interactionType = requestedAction,
        ))

        return if (requestedAction == CommentInteractionType.LIKE) {
            // 저장된게 싫어요였을 것이므로 싫어요는 하나 빼고, 좋아요를 하나 늘린다
            listOf(
                UpdateCommentRequest.Interaction(type = CommentInteractionType.DISLIKE, isCancelAction = true),
                UpdateCommentRequest.Interaction(type = CommentInteractionType.LIKE),
            )
        } else {
            // 저장된게 좋아요였을 것이므로 좋아요는 하나 빼고, 싫어요를 하나 늘린다
            listOf(
                UpdateCommentRequest.Interaction(type = CommentInteractionType.LIKE, isCancelAction = true),
                UpdateCommentRequest.Interaction(type = CommentInteractionType.DISLIKE),
            )
        }
    }
}