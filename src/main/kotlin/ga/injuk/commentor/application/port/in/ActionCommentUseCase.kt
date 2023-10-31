package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.request.ActionCommentRequest

interface ActionCommentUseCase : UseCase<ActionCommentRequest, Unit> {

    enum class ActionType(val value: String) {
        NEW("NEW"),
        CANCEL("CANCEL"),
        SWITCH("SWITCH");

        companion object {
            fun from(value: String?): ActionType? = ActionType.values()
                .find { it.value == value }
        }
    }
}