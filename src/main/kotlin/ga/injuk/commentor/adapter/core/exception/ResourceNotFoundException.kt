package ga.injuk.commentor.adapter.core.exception

import ga.injuk.commentor.common.CommentorError
import ga.injuk.commentor.common.ErrorDetail

class ResourceNotFoundException(
    private val msg: String? = null,
): CommentorError, RuntimeException() {
    override val errorDetails: ErrorDetail
        get() = ErrorDetail(
            code = "COMMENTOR_RESOURCE_NOT_FOUND_EXCEPTION",
            messages = msg?.let { listOf(it) } ?: listOf("Resource not found."),
        )
}