package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.Pagination
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.dto.request.ListSubCommentsRequest
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment

interface ListSubCommentsPort {

    fun getList(user: User, request: ListSubCommentsRequest): Pagination<Comment>
}