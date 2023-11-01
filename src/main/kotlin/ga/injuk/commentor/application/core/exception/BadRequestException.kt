package ga.injuk.commentor.application.core.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class BadRequestException(
    private val msg: String? = null,
) : CommentorError, RuntimeException() {
    override val errorDetails: ErrorDetail
        get() = ErrorDetail(
            code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
            messages = msg?.let { listOf(it) } ?: listOf("Bad request."),
        )
}