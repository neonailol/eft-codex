package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import eft.weapons.builds.Locale.itemName
import eft.weapons.builds.Locale.itemShortName
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlots
import org.apache.commons.collections4.comparators.ComparatorChain
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList
import kotlin.math.roundToInt

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
        "assets",
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

    private var locale: TestBackendLocale = loadBytes("locale.json")
    private val alternate: MutableMap<String, String> = HashMap()

    init {
        alternate["5b3baf8f5acfc40dc5296692"] = "116mm 7.62x25 TT barrel Gold"
    }

    fun itemName(id: String): String {
        val itemName = alternate.getOrDefault(id, locale.data.templates[id]?.name) ?: id
        return itemName.replace('\n', ' ')
    }

    fun itemShortName(id: String): String {
        val itemName = alternate.getOrDefault(id, locale.data.templates[id]?.shortName) ?: id
        return itemName.replace('\n', ' ')
    }
}

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
    val res = slots.asSequence().filter { it.id != "EMPTY" }.map { Items[it.id] }.map { item ->
        when {
            conflictsWithOtherMod(item, slots) -> false
            goesIntoWeapon(weapon, item) -> true
            else -> goesToOtherSlot(slots, item)
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
    val res = slots.asSequence().filter { it.id != "EMPTY" }.map { slot ->
        val si = Items[slot.id]
        if (si.props.slots.isNotEmpty()) {
            si.props.slots.asSequence().flatMap { it.props.filters.asSequence() }.flatMap { it.filter.asSequence() }.contains(item.id)
        } else {
            false
        }
    }
    return res.any { it }
}

fun goesIntoWeapon(weapon: TestItemTemplatesData, item: TestItemTemplatesData): Boolean {
    return weapon.props.slots.asSequence().flatMap { it.props.filters.asSequence() }.flatMap { it.filter.asSequence() }.contains(item.id)
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
    private val items: MutableSet<String>,
    val required: Boolean
) {

    constructor(item: TestItemTemplatesDataPropsSlots) :
        this(
            item.name,
            item.props.filters.flatMap { it.filter }.filter { isMatters(it) }.toMutableSet(),
            item.required
        )

    fun items(): MutableSet<String> = items

    fun addItems(newItems: Set<String>) {
        items.addAll(newItems.filter { isMatters(it) })
    }

    fun toSlots(): MutableCollection<Slot> {
        val toMutableList = items.map { Slot(it, name, required) }.toMutableList()
        if (! required) {
            toMutableList.add(Slot("EMPTY", name, required))
        }
        return toMutableList
    }
}

fun isMatters(itemId: String): Boolean {
    val item = Items[itemId]
    if (haveParentNamed(item, listOf("Sights", "TacticalCombo", "Flashlight", "Magazine", "Ammo", "Launcher"))) {
        return false
    }
    if (isScopeOrTacticalOnlyMount(item)) {
        return false
    }
    return true
}

fun isScopeOrTacticalOnlyMount(item: TestItemTemplatesData): Boolean {
    if (item.props.slots.isEmpty()) {
        return false
    }
    return item.props.slots
        .flatMap { it.props.filters }
        .flatMap { it.filter }
        .map { Items[it] }
        .all { haveParentNamed(it, listOf("TacticalCombo", "Flashlight", "Sights")) || isScopeOrTacticalOnlyMount(it) }
}

fun haveParentNamed(item: TestItemTemplatesData, excluded: Collection<String>): Boolean {
    var parent = item.parent
    while (parent != "") {
        val parentItem = Items[parent]
        if (excluded.contains(parentItem.name)) {
            return true
        }
        parent = parentItem.parent
    }
    return false
}

data class Slot(
    val id: String,
    val slot: String,
    val required: Boolean
)

data class WeaponBuild(
    private val weapon: TestItemTemplatesData,
    private val slots: List<Slot>
) {

    fun weapon() = weapon

    fun mods() = slots.filter { it.id != "EMPTY" }.map { Items[it.id] }

    fun slots() = slots

    fun modsErgo() = mods().map { it.props.ergonomics }.sum()

    fun modsRecoil() = mods().map { it.props.recoil }.sum()

    fun totalErgo() = (weapon.props.ergonomics + modsErgo()).roundToInt()

    fun totalRecoil() = (weapon.props.recoilForceUp * (1 + (modsRecoil() / 100))).roundToInt()

    fun modsNames() = mods().map { itemName(it.id) }

    fun slot(name: String): TestItemTemplatesData? {
        return when (val slot = slots.firstOrNull { it.slot == name }?.id) {
            null -> null
            "EMPTY" -> null
            else -> Items[slot]
        }
    }
}

fun weaponBuilds(weapon: TestItemTemplatesData): List<WeaponBuild> {
    val combinations = weaponCombinations(weapon).map { it.toSlots() }
    return permutations(weapon, combinations)
        .map { WeaponBuild(weapon, it) }
}

fun permutations(weapon: TestItemTemplatesData, collections: List<Collection<Slot>>): MutableCollection<MutableList<Slot>> {
    if (collections.isNullOrEmpty()) {
        return ArrayList()
    }
    val res: MutableCollection<MutableList<Slot>> = LinkedList()
    permutationsImpl(weapon, collections, res, 0, ArrayList())
    return res
}

fun permutationsImpl(
    weapon: TestItemTemplatesData,
    origin: List<Collection<Slot>>,
    result: MutableCollection<MutableList<Slot>>,
    depth: Int,
    current: MutableList<Slot>
) {
    if (depth == origin.size) {
        if (isValidBuild(weapon, current)) {
            result.add(current)
        }
        return
    }
    val currentCollection = origin[depth]
    for (element in currentCollection) {
        val copy: ArrayList<Slot> = ArrayList(origin.size)
        copy.addAll(current)
        copy.add(element)
        permutationsImpl(weapon, origin, result, depth + 1, copy)
    }
}

fun weaponCombinations(weapon: TestItemTemplatesData): MutableList<SlotVariant> {
    val slotVariants: MutableList<SlotVariant> = LinkedList()
    moreCombinations(listOf(weapon), slotVariants)
    return slotVariants.filter { it.items().isNotEmpty() }.toMutableList()
}

fun moreCombinations(
    map: List<TestItemTemplatesData>,
    slotVariants: MutableList<SlotVariant>
) {
    map.filter { it.props.slots.isNotEmpty() }
        .flatMap { it.props.slots }
        .map { SlotVariant(it) }
        .forEach {
            if (it.name == "mod_stock" && it.items().size == 1 && it.items().contains("5a0c59791526d8dba737bba7")) {
                // Recoil pad from GP-25 for AK Accessory Kit
                val pad = SlotVariant("recoil_pad", it.items(), it.required)
                val found = slotVariants.find { v -> pad.name == v.name }
                when {
                    found != null -> found.addItems(pad.items())
                    else -> slotVariants.add(pad)
                }
            } else {
                val found = slotVariants.find { v -> it.name == v.name }
                when {
                    found != null -> found.addItems(it.items())
                    else -> slotVariants.add(it)
                }
            }
            moreCombinations(it.items().map { n -> Items[n] }, slotVariants)
        }
}

fun printBuilds(
    weapon: TestItemTemplatesData,
    builds: List<WeaponBuild>
) {
    val slots = builds.flatMap { it.slots() }.map { it.slot }.distinct()
    val stringBuilder = StringBuilder()
    val csvPrinter = CSVPrinter(stringBuilder, CSVFormat.DEFAULT)
    val header = mutableListOf("Recoil", "Ergo").also { it.addAll(slots) }
    csvPrinter.printRecord(header)
    val comparator = ComparatorChain<WeaponBuild>(listOf(compareBy { it.totalRecoil() }, compareByDescending { it.totalErgo() }))
    for (build in builds.sortedWith(comparator)) {
        if (build.totalRecoil() == 53 && build.totalErgo() == 67) {
            println()
        }
        val recoil = build.totalRecoil().toString()
        val ergo = build.totalErgo().toString()
        val mods = slots.map { build.slot(it) }.map {
            when (it) {
                null -> ""
                else -> itemName(it.id)
            }
        }
        csvPrinter.printRecord(mutableListOf(recoil, ergo).also { it.addAll(mods) })
    }
    val message = stringBuilder.toString()
    println(message)

    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csv"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${itemShortName(weapon.id)}.csv")
    if (csvFile.toFile().exists()) {
        Files.write(csvFile, listOf(message))
    } else {
        Files.write(Files.createFile(csvFile), listOf(message))
    }
}

fun main(args: Array<String>) {
    println(args)
}
