package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import eft.weapons.builds.items.templates.TestItemTemplatesItem

fun mapper(): ObjectMapper {
    return ObjectMapper().findAndRegisterModules().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
}

fun stringBuilder(any: Any): String {
    return mapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(any)
}

data class TestItemTemplates(
    @JsonProperty("err")
    val error: Double,
    @JsonProperty("errmsg")
    val errorMessage: String?,
    val data: TestItemTemplatesData
)

class TestItemTemplatesData : HashMap<String, TestItemTemplatesItem>()

fun main(args: Array<String>) {

    // Try PM Pistol, TT Pistol, TOZ shotgun
    println(args)
}
