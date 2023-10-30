package ga.injuk.commentor.application.core.factory

import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.domain.model.InteractionStrategy

interface CommentInteractionStrategyFactory {

    fun from(strategy: InteractionStrategy): CommentInteractionStrategy
}