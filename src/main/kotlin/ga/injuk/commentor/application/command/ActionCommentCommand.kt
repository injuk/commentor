package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.*
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType
import ga.injuk.commentor.domain.model.CommentInteractionType.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActionCommentCommand(
    private val getCommentPort: GetCommentPort,
    private val updateCommentPort: UpdateCommentPort,

    private val createCommentInteractionPort: CreateCommentInteractionPort,
    private val getCommentInteractionPort: GetCommentInteractionPort,
    private val deleteCommentInteractionPort: DeleteCommentInteractionPort,
    private val updateCommentInteractionPort: UpdateCommentInteractionPort,
): ActionCommentUseCase {

    @Transactional
    override fun execute(user: User, data: ActionCommentRequest) {
        val (commentId, requestedAction) = data

        getCommentPort.get(user, GetCommentRequest(commentId, withLock = true))
            ?: throw ResourceNotFoundException("there is no comment")

        val commentInteraction = getCommentInteractionPort.get(user, GetCommentInteractionRequest(commentId, withLock = true))
        val interactions = if(commentInteraction == null) {
            getInteractionForNewAction(user, commentId, requestedAction)
        } else if(requestedAction == commentInteraction.type) {
            getInteractionForCancelAction(user, commentId, requestedAction)
        } else {
            getInteractionForSwitchAction(user, commentId, requestedAction)
        }

        updateCommentPort.update(user, UpdateCommentRequest(commentId, interactions = interactions))
    }

    private fun getInteractionForNewAction(user: User, commentId: Long, requestedAction: CommentInteractionType): List<UpdateCommentRequest.Interaction> {
        createCommentInteractionPort.create(user, CreateCommentInteractionRequest(
            commentId = commentId,
            interactionType = requestedAction,
        ))

        return listOf(UpdateCommentRequest.Interaction(type = requestedAction))
    }

    private fun getInteractionForCancelAction(user: User, commentId: Long, requestedAction: CommentInteractionType): List<UpdateCommentRequest.Interaction> {
        deleteCommentInteractionPort.delete(user, DeleteCommentInteractionRequest(commentId = commentId))

        return listOf(UpdateCommentRequest.Interaction(
            type = requestedAction,
            isCancelAction = true,
        ))
    }

    private fun getInteractionForSwitchAction(user: User, commentId: Long, requestedAction: CommentInteractionType): List<UpdateCommentRequest.Interaction> {
        updateCommentInteractionPort.update(user, UpdateCommentInteractionRequest(
            commentId = commentId,
            interactionType = requestedAction,
        ))

        return if(requestedAction == LIKE) {
            // 저장된게 싫어요였을 것이므로 싫어요는 하나 빼고, 좋아요를 하나 늘린다
            listOf(
                UpdateCommentRequest.Interaction(type = DISLIKE, isCancelAction = true),
                UpdateCommentRequest.Interaction(type = LIKE),
            )
        } else {
            // 저장된게 좋아요였을 것이므로 좋아요는 하나 빼고, 싫어요를 하나 늘린다
            listOf(
                UpdateCommentRequest.Interaction(type = LIKE, isCancelAction = true),
                UpdateCommentRequest.Interaction(type = DISLIKE),
            )
        }
    }
}