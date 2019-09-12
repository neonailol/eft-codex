package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.items.templates.TestItemTemplatesData
import org.testng.collections.Lists
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
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
    fun `can load items`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        var testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
    }

    @Test
    fun `can load locale`() {
        val mapper = mapper()
        val json = testData("TestBackendLocaleEn.bytes")
        var testItemTemplates = mapper.readValue(json, TestBackendLocale::class.java)
    }

    @Test
    fun `can find all weapons`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data
        testItemTemplates.data.values.asSequence()
            .filter { it.parent == "5447b5cf4bdc2d65278b4567" }
            .forEach { println(it) }
    }

    @Test
    fun `can list pm attachments`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        val weapon = testItemTemplates.data.values.asSequence()
            .filter { it.id == "5448bd6b4bdc2dfc2f8b4569" }
            .first()
        val magazines = weapon.props.slots.asSequence()
            .filter { it.name == "mod_magazine" }
            .first().props.filters.asSequence().flatMap { it.filter.asSequence() }
            .map {
                testItemTemplates.data.values.asSequence()
                    .filter { f -> f.id == it }
                    .first()
            }.toList()

        println("Weapon: ${itemName(weapon.id)} Ergo: ${weapon.props.ergonomics}")
        magazines.forEach {
            println("Weapon: ${itemName(weapon.id)} Mag: ${itemName(it.id)} Ergo: ${weapon.props.ergonomics + it.props.ergonomics}")
        }
    }

    @Test
    fun `can list all tt attachments`() {
        val testItemTemplates = loadBytes("TestItemTemplates.bytes", TestItemTemplates::class.java)
        val weapon = testItemTemplates.getItem("571a12c42459771f627b58a0")
        val slots = weapon.props.slots.map { SlotVariant(it.name, it.props.filters.flatMap { p -> p.filter }, it.required) }
        println(weapon)
        println(slots)
        println(permutations(listOf(listOf("A", "B", "C"), listOf("Q", "W", "E"))))
        println(permutations(slots.map { it.items }))
    }

    fun <T> permutations(collections: List<Collection<T>>): Collection<List<T>> {
        if (collections.isNullOrEmpty()) {
            return emptyList()
        }
        val res: MutableCollection<List<T>> = mutableListOf()
        permutationsImpl(collections, res, 0, mutableListOf<T>())
        return res
    }

    fun <T> permutationsImpl(ori: List<Collection<T>>, res: MutableCollection<List<T>>, d: Int, current: List<T>) {
        if (d == ori.size) {
            res.add(current)
            return
        }
        val currentCollection = ori[d]
        for (element in currentCollection) {
            val copy = Lists.newLinkedList(current)
            copy.add(element)
            permutationsImpl(ori, res, d+1, copy)
        }
    }

    @Test
    fun `list all parent types`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data.values.asSequence()
            .map { it.parent }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach {
                val parent = testItemTemplates.data.get(it)
                println("${parent?.id} - ${parent?.name}")
            }

        val first = testItemTemplates.data.values.first { it.parent == "" }
        println("Root: " + first.id)
    }

    @Test
    fun `build items hierarchy`() {
        val mapper = mapper()
        val json = testData("TestItemTemplates.bytes")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)

        val root = testItemTemplates.data.values.asSequence().filter { it.parent == "" }.first()

        val parents = testItemTemplates.data.values.asSequence()
            .distinctBy { it.parent }
            .filter { it.parent != "" }
            .map { testItemTemplates.data[it.parent] !! }
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
            .filter { it.parent == root.id }
            .toList()
        if (children.isEmpty()) {
            return emptyList()
        }
        return children.filter { parents.any { p -> p.id == it.id } }.map { ItemCategories(it, children(items, it, parents)) }
    }
}

@JsonPropertyOrder(value = ["rootName", "children"])
class ItemCategories(
    @JsonIgnore
    val root: TestItemTemplatesData,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemCategories> = listOf()
) {

    var rootName: String = root.name
}

data class SlotVariant(
    val name: String,
    val items: Collection<String>,
    val required: Boolean
)