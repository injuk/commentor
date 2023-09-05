package ga.injuk.commentor.adapter.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class InvalidJsonException(
    private val msg: String? = null,
): CommentorError, RuntimeException() {
    override val errorDetails: List<ErrorDetail>
        get() = listOf(
            ErrorDetail(
                code = "INVALID_JSON_EXCEPTION",
                message = msg ?: "Invalid JSON.",
            ),
        )
}