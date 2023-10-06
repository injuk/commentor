package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment

interface GetCommentPort {

    fun get(user: User, request: GetCommentRequest): Comment?
}