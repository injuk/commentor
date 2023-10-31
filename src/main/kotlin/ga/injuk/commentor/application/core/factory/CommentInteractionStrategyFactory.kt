package ga.injuk.commentor.application.core.factory

import ga.injuk.commentor.application.core.strategy.CommentInteractionStrategy
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase

interface CommentInteractionStrategyFactory {

    fun from(strategy: ActionCommentUseCase.ActionType): CommentInteractionStrategy
}