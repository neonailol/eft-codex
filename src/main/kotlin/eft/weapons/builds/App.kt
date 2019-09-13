package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlots
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList

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

fun openAsset(name: String): InputStream {
    val path = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "TextAsset",
        name
    )
    return Files.newInputStream(path)
}

inline fun <reified T : Any> loadBytes(name: String, clazz: Class<T>): T {
    val mapper = mapper()
    val json = openAsset(name)
    return mapper.readValue(json)
}

object Locale {

    var locale: TestBackendLocale

    init {
        val mapper = mapper()
        val json = openAsset("TestBackendLocaleRu.bytes")
        locale = mapper.readValue(json, TestBackendLocale::class.java)
    }

    fun itemName(id: String): String {
        return locale.data.templates[id]?.name ?: id
    }
}

fun TestItemTemplates.getItem(id: String): TestItemTemplatesData {
    return this.data[id] ?: throw IllegalStateException("Unknown id: $id")
}

fun <T> permutations(collections: List<Collection<T>>): MutableCollection<List<T>> {
    if (collections.isNullOrEmpty()) {
        return LinkedList()
    }
    val res: MutableCollection<List<T>> = mutableListOf()
    permutationsImpl(collections, res, 0, mutableListOf())
    return res
}

fun <T> permutationsImpl(ori: List<Collection<T>>, res: MutableCollection<List<T>>, d: Int, current: List<T>) {
    if (d == ori.size) {
        res.add(current)
        return
    }
    val currentCollection = ori[d]
    for (element in currentCollection) {
        val copy = LinkedList(current)
        copy.add(element)
        permutationsImpl(ori, res, d + 1, copy)
    }
}

fun children(
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
    val items: MutableCollection<String>,
    val required: Boolean
) {

    constructor(item: TestItemTemplatesDataPropsSlots) :
        this(
            item.name,
            item.props.filters.flatMap { p -> p.filter }.toMutableList(),
            item.required
        )

    fun toSlots(): MutableCollection<Slot> {
        val toMutableList = items.map { Slot(it, name, required) }.toMutableList()
        if (! required) {
            toMutableList.add(Slot("EMPTY", name, required))
        }
        return toMutableList
    }
}

data class Slot(
    val id: String,
    val slot: String,
    val required: Boolean
)

fun main(args: Array<String>) {
    // Try PM Pistol, TT Pistol, TOZ shotgun
    println(args)
}
