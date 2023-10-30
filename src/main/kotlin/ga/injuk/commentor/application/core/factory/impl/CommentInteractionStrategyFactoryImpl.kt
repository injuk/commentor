package ga.injuk.commentor.application.core.factory.impl

import ga.injuk.commentor.adapter.core.exception.UncaughtException
import ga.injuk.commentor.application.core.factory.CommentInteractionStrategyFactory
import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.domain.model.InteractionStrategy
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

    override fun from(strategy: InteractionStrategy): CommentInteractionStrategy = when (strategy) {
        InteractionStrategy.NEW_INTERACTION -> strategies[NEW_INTERACTION_STRATEGY_NAME]
        InteractionStrategy.CANCEL_INTERACTION -> strategies[CANCEL_INTERACTION_STRATEGY_NAME]
        InteractionStrategy.SWITCH_INTERACTION -> strategies[SWITCH_INTERACTION_STRATEGY_NAME]
    } ?: throw UncaughtException("There is no interaction strategies for ${strategy.value}")
}