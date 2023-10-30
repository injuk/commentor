package ga.injuk.commentor.domain.model

enum class InteractionStrategy(val value: String) {
    NEW_INTERACTION("NEW_INTERACTION"),
    CANCEL_INTERACTION("CANCEL_INTERACTION"),
    SWITCH_INTERACTION("SWITCH_INTERACTION");

    companion object {
        fun from(value: String?): InteractionStrategy? = InteractionStrategy.values()
            .find { it.value == value }
    }
}