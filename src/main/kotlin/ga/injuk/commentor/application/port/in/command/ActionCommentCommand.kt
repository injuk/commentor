package ga.injuk.commentor.application.port.`in`.command

import ga.injuk.commentor.adapter.core.exception.BadRequestException
import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.core.factory.CommentInteractionStrategyFactory
import ga.injuk.commentor.application.port.dto.request.ActionCommentRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.GetCommentInteractionPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.application.port.out.persistence.UpdateCommentPort
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.InteractionStrategy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActionCommentCommand(
    private val getCommentPort: GetCommentPort,
    private val updateCommentPort: UpdateCommentPort,
    private val getCommentInteractionPort: GetCommentInteractionPort,

    private val strategyFactory: CommentInteractionStrategyFactory,
) : ActionCommentUseCase {

    @Transactional
    override fun execute(user: User, data: ActionCommentRequest) {
        val (commentId, requestedAction) = data

        val comment = getCommentPort.get(user, GetCommentRequest(commentId, withLock = true))
            ?: throw ResourceNotFoundException("There is no comment")
        if (comment.isDeleted) {
            throw BadRequestException("Cannot action for deleted comment")
        }

        val oldInteraction = getCommentInteractionPort.get(
            user = user,
            request = GetCommentInteractionRequest(commentId, withLock = true)
        )

        val currentStrategy = strategyFactory.from(
            if (oldInteraction == null) {
                InteractionStrategy.NEW_INTERACTION
            } else if (requestedAction == oldInteraction.type) {
                InteractionStrategy.CANCEL_INTERACTION
            } else {
                InteractionStrategy.SWITCH_INTERACTION
            }
        )

        updateCommentPort.update(user, UpdateCommentRequest(
            id = commentId,
            interactions = currentStrategy.getInteractions(user, commentId, requestedAction)
        ))
    }
}