package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper

fun mapper(): ObjectMapper {
    return ObjectMapper().findAndRegisterModules().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
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
