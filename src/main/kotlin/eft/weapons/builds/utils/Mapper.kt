package eft.weapons.builds.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

object Mapper {

    private val mapper: ObjectMapper = ObjectMapper()
        .findAndRegisterModules()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)

    operator fun invoke(): ObjectMapper {
        return mapper
    }
}

fun mapper(): ObjectMapper {
    return Mapper()
}

fun stringBuilder(any: Any): String {
    return mapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(any)
}
