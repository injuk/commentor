package ga.injuk.commentor.application.core.factory.impl

import ga.injuk.commentor.application.core.exception.UncaughtException
import ga.injuk.commentor.application.core.factory.CommentInteractionStrategyFactory
import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase.ActionType
import org.springframework.stereotype.Component

@Component
class CommentInteractionStrategyFactoryImpl(
    val strategies: Map<String, CommentInteractionStrategy>,
) : CommentInteractionStrategyFactory {
    companion object {
        const val NEW_INTERACTION_STRATEGY_NAME = "newInteractionStrategy"
        const val CANCEL_INTERACTION_STRATEGY_NAME = "cancelInteractionStrategy"
        const val SWITCH_INTERACTION_STRATEGY_NAME = "switchInteractionStrategy"
    }

    override fun from(actionType: ActionType): CommentInteractionStrategy = when (actionType) {
        ActionType.NEW -> strategies[NEW_INTERACTION_STRATEGY_NAME]
        ActionType.CANCEL -> strategies[CANCEL_INTERACTION_STRATEGY_NAME]
        ActionType.SWITCH -> strategies[SWITCH_INTERACTION_STRATEGY_NAME]
    } ?: throw UncaughtException("There is no interaction strategies for ${actionType.value}")
}