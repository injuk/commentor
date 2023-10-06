package ga.injuk.commentor.application.port.dto.request

data class ListSubCommentsRequest(
    val parentId: Long,

    val limit: Long? = 20L,
    val nextCursor: String? = null,
    val sortConditions: SortConditions = SortConditions(),
) {
    data class SortConditions(
        val criteria: Criteria = Criteria.CREATED_AT,
        val order: Order = Order.DESC,
    )

    enum class Criteria(val value: String) {
        CREATED_AT("CREATED_AT"),
        UPDATED_AT("UPDATED_AT"),
    }

    enum class Order(val value: String) {
        ASC("ASC"),
        DESC("DESC"),
    }
}