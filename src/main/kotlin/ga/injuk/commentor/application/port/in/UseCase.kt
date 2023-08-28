package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.domain.User

interface UseCase<in Req, out Res> {
    fun execute(user: User, data: Req): Res
}