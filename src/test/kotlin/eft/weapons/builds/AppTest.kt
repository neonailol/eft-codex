package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataProps
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.full.memberProperties
import kotlin.test.Test

class AppTest {

    fun testData(name: String): InputStream {
        val path = Paths.get(
            Paths.get(System.getProperty("user.dir")).toString(),
            "TextAsset",
            name
        )
        return Files.newInputStream(path)
    }

    @Test
    fun `can load some json`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        var testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
    }

    @Test
    fun `can find all weapons`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data
        testItemTemplates.data.values.asSequence()
            .filter { it._parent == "5447b5cf4bdc2d65278b4567" }
            .forEach { println(it) }
    }

    @Test
    fun `can list pm attachments`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        val weapon = testItemTemplates.data.values.asSequence()
            .filter { it._id == "5448bd6b4bdc2dfc2f8b4569" }
            .first()
        val magazines = weapon._props.Slots.asSequence()
            .filter { it._name == "mod_magazine" }
            .first()._props.filters.asSequence().flatMap { it.Filter.asSequence() }
            .map {
                testItemTemplates.data.values.asSequence()
                    .filter { f -> f._id == it }
                    .first()
            }.toList()

        println("Weapon: ${weapon._name} Ergo: ${weapon._props.Ergonomics}")
        magazines.forEach {
            println("Weapon: ${weapon._name} Mag: ${it._name} Ergo: ${weapon._props.Ergonomics + it._props.Ergonomics}")
        }
    }

    @Test
    fun `can find all params of weapons`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        val validProps = mutableSetOf<String>()
        val invalidProps = mutableSetOf<String>()
        testItemTemplates.data.values.asSequence()
            .filter { it._parent == "5447b6254bdc2dc3278b4568" }
            .forEach {
                for (memberProperty in TestItemTemplatesDataProps::class.memberProperties) {
                    if (memberProperty.get(it._props) != null) {
                        validProps.add(memberProperty.name)
                    } else {
                        invalidProps.add(memberProperty.name)
                    }
                }
            }
        println(validProps)
        println(invalidProps)
    }

    @Test
    fun `list all parent types`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data.values.asSequence()
            .map { it._parent }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach {
                val parent = testItemTemplates.data.get(it)
                println("${parent?._id} - ${parent?._name}")
            }

        val first = testItemTemplates.data.values.first { it._parent == "" }
        println("Root: " + first._id)
    }

    @Test
    fun `build items hierarchy`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)

        val root = testItemTemplates.data.values.asSequence().filter { it._parent == "" }.first()

        val parents = testItemTemplates.data.values.asSequence()
            .distinctBy { it._parent }
            .filter { it._parent != "" }
            .map { testItemTemplates.data[it._parent] !! }
            .toList()

        val tree = ItemCategories(root, children(testItemTemplates, root, parents))

        println(stringBuilder(tree))
    }

    private fun children(
        items: TestItemTemplates,
        root: TestItemTemplatesData,
        parents: List<TestItemTemplatesData>
    ): List<ItemCategories> {
        val children = items.data.values.asSequence()
            .filter { it._parent == root._id }
            .toList()
        if (children.isEmpty()) {
            return emptyList()
        }
        return children.filter { parents.any { p -> p._id == it._id } }.map { ItemCategories(it, children(items, it, parents)) }
    }
}

@JsonPropertyOrder(value = ["rootName", "children"])
class ItemCategories(
    @JsonIgnore
    val root: TestItemTemplatesData,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemCategories> = listOf()
) {

    var rootName: String = root._name
}