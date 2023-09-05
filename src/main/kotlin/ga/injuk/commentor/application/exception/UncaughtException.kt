package ga.injuk.commentor.application.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class UncaughtException(
    private val msg: String? = null,
): CommentorError, RuntimeException() {
    override val errorDetails: List<ErrorDetail>
        get() = listOf(
            ErrorDetail(
                code = "COMMENTOR_UNCAUGHT_EXCEPTION",
                message = msg ?: "Uncaught exception occurred.",
            ),
        )
}