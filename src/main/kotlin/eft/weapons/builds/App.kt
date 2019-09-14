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

inline fun <reified T : Any> loadBytes(name: String): T {
    val mapper = mapper()
    val json = openAsset(name)
    return mapper.readValue(json)
}

object Locale {

    private var locale: TestBackendLocale = loadBytes("TestBackendLocaleEn.bytes")
    private val alternate: MutableMap<String, String> = HashMap()

    init {
        alternate["5b3baf8f5acfc40dc5296692"] = "116mm 7.62x25 TT barrel Gold"
    }

    fun itemName(id: String): String {
        val itemName = alternate.getOrDefault(id, locale.data.templates[id]?.name) ?: id
        return itemName.replace('\n', ' ')
    }
}

object Items {
    private val testItemTemplates = loadBytes("TestItemTemplates.bytes") as TestItemTemplates

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

fun <T> permutations(collections: List<Collection<T>>): MutableCollection<MutableList<T>> {
    if (collections.isNullOrEmpty()) {
        return LinkedList()
    }
    val res: MutableCollection<MutableList<T>> = mutableListOf()
    permutationsImpl(collections, res, 0, mutableListOf())
    return res
}

fun <T> permutationsImpl(ori: List<Collection<T>>, res: MutableCollection<MutableList<T>>, d: Int, current: MutableList<T>) {
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
    root: TestItemTemplatesData,
    parents: List<TestItemTemplatesData>
): List<ItemCategories> {
    val children = Items.children(root.id)
    if (children.isEmpty()) {
        return emptyList()
    }
    return children.filter { parents.any { p -> p.id == it.id } }.map { ItemCategories(it, children(it, parents)) }
}

fun isValidBuild(
    weapon: TestItemTemplatesData,
    slots: Collection<Slot>
): Boolean {
    val res = slots.filter { it.id != "EMPTY" }.map { Items[it.id] }.map { item ->
        if (conflictsWithOtherMod(item, slots)) {
            false
        } else if (goesIntoWeapon(weapon, item)) {
            true
        } else {
            goesToOtherSlot(slots, item)
        }
    }
    return res.all { it }
}

fun conflictsWithOtherMod(item: TestItemTemplatesData, slots: Collection<Slot>): Boolean {
    for (conflictingItem in item.props.conflictingItems) {
        if (slots.any { it.id == conflictingItem }) {
            return true
        }
    }
    return false
}

fun goesToOtherSlot(
    slots: Collection<Slot>,
    item: TestItemTemplatesData
): Boolean {
    val res = slots.filter { it.id != "EMPTY" }.map { slot ->
        val si = Items[slot.id]
        if (si.props.slots.isNotEmpty()) {
            si.props.slots.flatMap { it.props.filters }.flatMap { it.filter }.contains(item.id)
        } else {
            false
        }
    }
    return res.any { it }
}

fun goesIntoWeapon(weapon: TestItemTemplatesData, item: TestItemTemplatesData): Boolean {
    return weapon.props.slots.flatMap { it.props.filters }.flatMap { it.filter }.contains(item.id)
}

@JsonPropertyOrder(value = ["id", "name", "children"])
class ItemCategories(
    @JsonIgnore
    val root: TestItemTemplatesData,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemCategories> = listOf()
) {

    var id: String = root.id
    var name: String = root.name
}

data class SlotVariant(
    val name: String,
    val items: MutableSet<String>,
    val required: Boolean
) {

    constructor(item: TestItemTemplatesDataPropsSlots) :
        this(
            item.name,
            item.props.filters.flatMap { p -> p.filter }.toMutableSet(),
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

data class WeaponBuild(
    val weapon: TestItemTemplatesData,
    val mods: List<Slot>
)

fun main(args: Array<String>) {
    // Try PM Pistol, TT Pistol, TOZ shotgun
    println(args)
}
