package eft.weapons.builds

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.Test

class AppTest {

    @Test
    fun `can load some json`() {
        val mapper = ObjectMapper().findAndRegisterModules()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        var testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
    }
}
