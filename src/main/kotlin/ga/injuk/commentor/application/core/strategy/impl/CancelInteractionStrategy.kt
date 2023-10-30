package ga.injuk.commentor.application.core.strategy.impl

import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.application.port.dto.request.DeleteCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.out.persistence.DeleteCommentInteractionPort
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.springframework.stereotype.Component

@Component
class CancelInteractionStrategy(
    private val deleteCommentInteractionPort: DeleteCommentInteractionPort,
) : CommentInteractionStrategy {

    override fun getInteractions(
        user: User,
        commentId: Long,
        requestedAction: CommentInteractionType,
    ): List<UpdateCommentRequest.Interaction> {
        deleteCommentInteractionPort.delete(user, DeleteCommentInteractionRequest(commentId = commentId))

        return listOf(UpdateCommentRequest.Interaction(
            type = requestedAction,
            isCancelAction = true,
        ))
    }
}