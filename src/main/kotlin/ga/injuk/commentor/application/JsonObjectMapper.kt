package ga.injuk.commentor.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

internal object JsonObjectMapper {
    private val mapper: ObjectMapper = ObjectMapper().registerModules(
        KotlinModule.Builder()
            .build()
    )

    fun instance(): ObjectMapper = mapper
}