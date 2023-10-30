package ga.injuk.commentor.application.core.strategy.impl

import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.application.port.dto.request.CreateCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.out.persistence.CreateCommentInteractionPort
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.springframework.stereotype.Component

@Component
class NewInteractionStrategy(
    private val createCommentInteractionPort: CreateCommentInteractionPort,
) : CommentInteractionStrategy {

    override fun getInteractions(
        user: User,
        commentId: Long,
        requestedAction: CommentInteractionType,
    ): List<UpdateCommentRequest.Interaction> {
        createCommentInteractionPort.create(user, CreateCommentInteractionRequest(
            commentId = commentId,
            interactionType = requestedAction,
        ))

        return listOf(UpdateCommentRequest.Interaction(type = requestedAction))
    }
}