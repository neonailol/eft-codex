package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonProperty

data class TestItemTemplates(
    @JsonProperty("err")
    val error: Double,
    @JsonProperty("errmsg")
    val errorMessage: String?,
    val data: TestItemTemplatesData
)

class TestItemTemplatesData : HashMap<String, TestItemTemplatesItem>()

fun main(args: Array<String>) {
    println(args)
}
