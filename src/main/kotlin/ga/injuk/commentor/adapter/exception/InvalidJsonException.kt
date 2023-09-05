package ga.injuk.commentor.adapter.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class InvalidJsonException(
    private val msg: String? = null,
): CommentorError, RuntimeException() {
    override val errorDetails: ErrorDetail
        get() = ErrorDetail(
            code = "INVALID_JSON_EXCEPTION",
            messages = msg?.let { listOf(it) } ?: listOf("Invalid JSON."),
        )
}