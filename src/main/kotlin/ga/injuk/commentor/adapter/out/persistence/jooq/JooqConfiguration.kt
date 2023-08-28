package ga.injuk.commentor.adapter.out.persistence.jooq

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class JooqConfiguration {

    @Bean
    fun dslContext(dataSource: DataSource): DSLContext? = DefaultConfiguration().run {
        set(SQLDialect.POSTGRES)
        set(dataSource)

        DefaultDSLContext(this)
    }
}