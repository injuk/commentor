package ga.injuk.commentor.application.port.dto

data class Pagination<T>(
    val results: List<T>,
    val nextCursor: String? = null,
)