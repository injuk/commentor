package ga.injuk.commentor.application.command

import ga.injuk.commentor.application.port.dto.request.ActionCommentRequest
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.DeleteCommentInteractionPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentInteractionPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.application.port.out.persistence.UpsertCommentInteractionPort
import ga.injuk.commentor.domain.User
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

    }
}