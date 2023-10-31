package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.request.ActionCommentRequest

interface ActionCommentUseCase : UseCase<ActionCommentRequest, Unit> {

    enum class ActionType(val value: String) {
        NEW_INTERACTION("NEW_INTERACTION"),
        CANCEL_INTERACTION("CANCEL_INTERACTION"),
        SWITCH_INTERACTION("SWITCH_INTERACTION");

        companion object {
            fun from(value: String?): ActionType? = ActionType.values()
                .find { it.value == value }
        }
    }
}