package ga.injuk.commentor.common

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Component
class TransactionHelper(
    private val transactionManager: PlatformTransactionManager,
) {
    companion object {
        enum class Propagation(val behavior: Int) {
            REQUIRED(0),
            SUPPORTS(1),
            MANDATORY(2),

            REQUIRES_NEW(3),
            NOT_SUPPORTED(4),
            NEVER(5),
            NESTED(6),
        }
    }

    fun <T> execute(propagation: Propagation = Propagation.REQUIRED, block: () -> T): T? {
        return TransactionTemplate(transactionManager)
            .apply { propagationBehavior = propagation.behavior }
            .execute<T> { block() }
    }
}