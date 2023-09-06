package ga.injuk.commentor.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object JsonObjectMapper {
    val mapper = ObjectMapper().registerModules(
        KotlinModule.Builder()
            .build()
    )

    fun instance() = mapper
}