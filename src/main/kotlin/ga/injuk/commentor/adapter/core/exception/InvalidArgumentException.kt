package ga.injuk.commentor.adapter.core.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class InvalidArgumentException(
    private val msg: String? = null,
): CommentorError, RuntimeException() {
    override val errorDetails: ErrorDetail
        get() = ErrorDetail(
            code = "COMMENTOR_INVALID_ARGUMENT_EXCEPTION",
            messages = msg?.let { listOf(it) } ?: listOf("Invalid argument requested."),
        )
}