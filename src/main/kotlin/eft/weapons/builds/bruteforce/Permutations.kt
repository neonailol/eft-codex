package eft.weapons.builds.bruteforce

import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlots
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale
import eft.weapons.builds.utils.isMatters
import org.apache.commons.collections4.comparators.ComparatorChain
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList
import kotlin.math.roundToInt

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

    constructor(
        parent: String,
        items: List<TestItemTemplatesData>,
        required: Boolean
    ) : this(
        parent,
        items.map { it.id }.toMutableSet(),
        required
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

fun slotName(item: TestItemTemplatesData): String {
    val itemName = Items[item.parent].name
    if (itemName == "FlashHider" || itemName == "Silencer" || itemName == "MuzzleCombo") {
        return "Muzzle"
    }
    if (item.id == "5a0c59791526d8dba737bba7") {
        return "RecoilPad"
    }
    if (item.id == "59ecc28286f7746d7a68aa8c") {
        return "StockAdapter"
    }
    return itemName
}

data class Slot(
    val id: String,
    val slot: String,
    val required: Boolean
)

data class WeaponBuild(
    private val weapon: TestItemTemplatesData,
    private val slots: Collection<Slot>
) {

    fun weapon() = weapon

    fun mods() = slots.filter { it.id != "EMPTY" }.map { Items[it.id] }

    fun slots() = slots

    fun modsErgo() = mods().map { it.props.ergonomics }.sum()

    fun modsRecoil() = mods().map { it.props.recoil }.sum()

    fun totalErgo() = (weapon.props.ergonomics + modsErgo()).roundToInt()

    fun totalRecoil() = (weapon.props.recoilForceUp * (1 + (modsRecoil() / 100))).roundToInt()

    fun modsNames() = mods().map { Locale.itemName(it.id) }

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

fun permutations(weapon: TestItemTemplatesData, collections: List<Collection<Slot>>): MutableCollection<Collection<Slot>> {
    if (collections.isNullOrEmpty()) {
        return ArrayList()
    }
    val res: MutableCollection<Collection<Slot>> = LinkedList()
    permutationsImpl(weapon, collections, res, 0, ArrayList())
    return res
}

fun permutationsImpl(
    weapon: TestItemTemplatesData,
    origin: List<Collection<Slot>>,
    result: MutableCollection<Collection<Slot>>,
    depth: Int,
    current: Collection<Slot>
) {
    if (depth == origin.size) {
        if (isValidBuild(weapon, current)) {
            result.add(current)
        }
        return
    }

    val currentCollection = origin[depth]
    for (element in currentCollection) {
        permutationsImpl(weapon, origin, result, depth + 1, current + element)
    }
}

fun weaponCombinations(weapon: TestItemTemplatesData): MutableList<SlotVariant> {
    val slotVariants: MutableList<SlotVariant> = LinkedList()
    moreCombinations(listOf(weapon), slotVariants)

//    return slotVariants.filter { it.items().isNotEmpty() }
//        .flatMap { it.items() }
//        .map { Items[it] }
//        .groupBy { slotName(it) }
//        .map { SlotVariant(it.key, it.value, false) }
//        .toMutableList()
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
): Int {
    val slots = builds.asSequence().flatMap { it.slots().asSequence() }.map { it.slot }.distinct().toList()
    val stringBuilder = StringBuilder()
    val csvPrinter = CSVPrinter(stringBuilder, CSVFormat.DEFAULT)
    val header = mutableListOf("Recoil", "Ergo").also { it.addAll(slots) }
    csvPrinter.printRecord(header)
    val comparator = ComparatorChain<WeaponBuild>(listOf(compareBy { it.totalRecoil() }, compareByDescending { it.totalErgo() }))
    for (build in builds.sortedWith(comparator)) {
        val recoil = build.totalRecoil().toString()
        val ergo = build.totalErgo().toString()
        val mods = slots.map { build.slot(it) }.map {
            when (it) {
                null -> ""
                else -> Locale.itemName(it.id)
            }
        }
        csvPrinter.printRecord(mutableListOf(recoil, ergo).also { it.addAll(mods) })
    }
    val message = stringBuilder.toString()

    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csv"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${Locale.itemShortName(weapon.id)}.csv")
    if (csvFile.toFile().exists()) {
        Files.write(csvFile, listOf(message))
    } else {
        Files.write(Files.createFile(csvFile), listOf(message))
    }
    return message.split('\n').size
}

class Permutations