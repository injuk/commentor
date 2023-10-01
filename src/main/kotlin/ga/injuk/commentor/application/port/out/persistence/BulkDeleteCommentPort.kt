package ga.injuk.commentor.application.port.out.persistence

import ga.injuk.commentor.application.port.dto.request.BulkDeleteCommentRequest

interface BulkDeleteCommentPort {
    fun delete(request: BulkDeleteCommentRequest): Int
}