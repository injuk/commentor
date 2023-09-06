package ga.injuk.commentor.adapter.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class UncaughtException(
    private val msg: String? = null,
): CommentorError, RuntimeException() {
    override val errorDetails: ErrorDetail
        get() = ErrorDetail(
            code = "COMMENTOR_UNCAUGHT_EXCEPTION",
            messages = msg?.let { listOf(it) } ?: listOf("Uncaught exception occurred."),
        )
}