package ga.injuk.commentor.domain

import java.util.*

data class RequestContext(
    val trace: Trace,
) {
    companion object {
        fun create() = RequestContext(
            trace = Trace(
                id = UUID.randomUUID().toString(),
            ),
        )
    }

    data class Trace(
        val id: String,
    )
}