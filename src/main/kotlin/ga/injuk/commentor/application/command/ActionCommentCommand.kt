package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.*
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest.InteractionType.*
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.DeleteCommentInteractionPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentInteractionPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.application.port.out.persistence.UpsertCommentInteractionPort
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentInteractionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActionCommentCommand(
    private val getCommentPort: GetCommentPort,
    private val getCommentInteractionPort: GetCommentInteractionPort,
    private val deleteCommentInteractionPort: DeleteCommentInteractionPort,
    private val upsertCommentInteractionPort: UpsertCommentInteractionPort,
): ActionCommentUseCase {

    @Transactional
    override fun execute(user: User, data: ActionCommentRequest) {
        val (commentId, requestedAction) = data
        val comment = getCommentPort.get(user, GetCommentRequest(commentId, withLock = true))
            ?: throw ResourceNotFoundException("there is no comment")

        var temp: UpdateCommentRequest.InteractionType? = null
        val commentInteraction = getCommentInteractionPort.get(user, GetCommentInteractionRequest(commentId, withLock = true))
        if(commentInteraction != null) {
            // 이미 좋아요 / 싫어요 한 댓글이다

            if(requestedAction == commentInteraction.type) {
                // 요청된 action이랑, 이미 있던 action이 같으므로 interaction을 취소한다
                deleteCommentInteractionPort.delete(user, DeleteCommentInteractionRequest(commentId))

                if(requestedAction == CommentInteractionType.LIKE) {
                    // likeCount를 하나 빼야 한다
                    temp = CANCEL_LIKE
                } else {
                    // dislikeCount를 하나 빼야 한다
                    temp = CANCEL_DISLIKE
                }
            } else {
                // 요청된 action이랑, 이미 있던 action이 다르므로 기존의 count는 빼고 현재 요청된 count는 추가해야 한다
                // TODO: 여기 해야함...

                upsertCommentInteractionPort.upsert(user, UpsertCommentInteractionRequest(
                    commentId = commentId,
                    interactionType = requestedAction,
                ))
            }
        } else {
            // 최초로 좋아요 / 싫어요 한 댓글이므로 그냥 추가한다
            upsertCommentInteractionPort.upsert(user, UpsertCommentInteractionRequest(
                commentId = commentId,
                interactionType = requestedAction,
            ))

            if(requestedAction == CommentInteractionType.LIKE) {
                temp = LIKE
            } else {
                temp = DISLIKE
            }
        }
    }
}