package ga.injuk.commentor

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import javax.sql.DataSource

@Configuration
class TestContainerConfiguration {

    @Bean
    @DependsOn("testContainerComponent")
    fun dataSource(): DataSource = DataSourceBuilder.create()
        .url("jdbc:postgresql://localhost:${TestContainerComponent.CONTAINER.getMappedPort(5432)}/${TestContainerComponent.DB_NAME}")
        .driverClassName("org.postgresql.Driver")
        .username(TestContainerComponent.USER)
        .password(TestContainerComponent.PASSWORD)
        .build()
}