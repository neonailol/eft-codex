package eft.weapons.builds

import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.items.templates.TestItemTemplatesData

object Items {
    private val testItemTemplates = loadBytes("items.json") as TestItemTemplates

    operator fun get(id: String): TestItemTemplatesData {
        return testItemTemplates.data[id] ?: throw IllegalStateException("Unknown id: $id")
    }

    fun children(id: String): List<TestItemTemplatesData> {
        return testItemTemplates.data
            .values
            .asSequence()
            .filter { it.parent == id }
            .toList()
    }

    fun all(): MutableCollection<TestItemTemplatesData> {
        return testItemTemplates.data.values
    }
}

