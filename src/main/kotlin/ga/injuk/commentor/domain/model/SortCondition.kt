package ga.injuk.commentor.domain.model


data class SortCondition(
    val criteria: Criteria = Criteria.CREATED_AT,
    val order: Order = Order.DESC,
) {
    enum class Criteria(val value: String) {
        CREATED_AT("CREATED_AT"),
        UPDATED_AT("UPDATED_AT"),
    }

    enum class Order(val value: String) {
        ASC("ASC"),
        DESC("DESC"),
    }
}