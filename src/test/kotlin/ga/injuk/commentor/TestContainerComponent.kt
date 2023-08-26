package ga.injuk.commentor

import org.springframework.stereotype.Component
import org.testcontainers.containers.PostgreSQLContainer
import javax.annotation.PreDestroy

@Component
class TestContainerComponent {
    companion object {
        const val DB_NAME = "commentor"
        const val USER = "tester"
        const val PASSWORD = "tester-password"

        @JvmStatic
        val CONTAINER = PostgreSQLContainer<Nothing>("postgres:13.8").apply {
            withDatabaseName(DB_NAME)
            withUsername(USER)
            withPassword(PASSWORD)
            start()
        }
    }
    @PreDestroy
    fun stop() {
        CONTAINER.stop()
    }
}