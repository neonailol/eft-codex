package eft.weapons.builds.tree

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlots
import eft.weapons.builds.tree.ItemTreeNodeType.ITEM
import eft.weapons.builds.tree.ItemTreeNodeType.META
import eft.weapons.builds.tree.ItemTreeNodeType.ROOT
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.haveParentNamed
import eft.weapons.builds.utils.isMatters
import eft.weapons.builds.utils.stringBuilder
import java.util.TreeSet
import kotlin.math.roundToInt

fun itemTree(weapon: TestItemTemplatesData): ItemTree {
    return ItemTree(weapon, "", ROOT, true, children(weapon))
}

fun children(item: TestItemTemplatesData): List<ItemTree> {
    return item.props.slots
        .filter { it.props.filters.isNotEmpty() }
        .map { it to children(it) }
        .filter { it.second.isNotEmpty() }
        .map { ItemTree(it.first, META, it.first.required, it.second) }
}

fun children(filter: TestItemTemplatesDataPropsSlots): List<ItemTree> {
    return filter.props.filters.first().filter
        .filter { isMatters(it) }
        .map { Items[it] }
        .map { ItemTree(it, filter.id, ITEM, false, children(it)) }
}

@JsonPropertyOrder(value = ["id", "name", "parent", "type", "required", "children"])
data class ItemTree(
    val id: String,
    val name: String,
    val parent: String,
    val type: ItemTreeNodeType,
    val required: Boolean,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemTree>,
    @JsonIgnore
    var parentTree: ItemTree? = null
) {

    constructor(
        item: TestItemTemplatesData,
        parent: String,
        type: ItemTreeNodeType,
        required: Boolean,
        children: List<ItemTree>
    ) : this(
        item.id,
        itemName(item.id),
        parent,
        type,
        required,
        children
    ) {
        children.forEach { it.parentTree = this }
    }

    constructor(
        item: TestItemTemplatesDataPropsSlots,
        type: ItemTreeNodeType,
        required: Boolean,
        children: List<ItemTree>
    ) : this(
        item.id,
        item.name,
        item.parent,
        type,
        required,
        children
    ) {
        children.forEach { it.parentTree = this }
    }
}

enum class ItemTreeNodeType { ROOT, META, ITEM }

fun transform(tree: ItemTree): MutableMap<String, MutableSet<String>> {
    val result: MutableMap<String, MutableSet<String>> = HashMap()
    processChildren(result, tree.children)
    return result
}

fun processChildren(result: MutableMap<String, MutableSet<String>>, children: List<ItemTree>) {
    for (child in children) {
        if (child.type == META) {
            val list = result.getOrDefault(child.id, TreeSet())
            list.addAll(child.children.map { it.id })
            if (child.required == false) {
                list.add("EMPTY")
            }
            result[child.id] = list
            processChildren(result, child.children)
        } else if (child.type == ITEM) {
            val list = result.getOrDefault(child.parent, TreeSet())
            list.add(child.id)
            result[child.parent] = list
            processChildren(result, child.children)
        }
    }
}

fun permutations(writer: ResultWriter, collections: MutableList<List<Slot>>) {
    permutations(writer, collections.sortedBy { it.size }, 0, ArrayList())
}

fun permutations(
    writer: ResultWriter,
    origin: List<Collection<Slot>>,
    depth: Int,
    current: Collection<Slot>
) {
    if (depth == origin.size) {
        writer.writeLineS(current)
        return
    }

    val currentCollection = origin[depth]
    for (element in currentCollection) {
        permutations(writer, origin, depth + 1, current + element)
    }
}

fun isValidBuild(
    weapon: TestItemTemplatesData,
    slots: Collection<String>
): Boolean {
    return slots.asSequence().filter { it != "EMPTY" }.map { Items[it] }.all { item ->
        when {
            conflictsWithOtherMod(item, slots) -> false
            goesIntoWeapon(weapon, item) -> true
            else -> goesToOtherSlot(slots, item)
        }
    }
}

fun conflictsWithOtherMod(item: TestItemTemplatesData, slots: Collection<String>): Boolean {
    for (conflictingItem in item.props.conflictingItems) {
        if (slots.any { it == conflictingItem }) {
            return true
        }
    }
    return false
}

fun goesToOtherSlot(
    slots: Collection<String>,
    item: TestItemTemplatesData
): Boolean {
    return slots.asSequence().filter { it != "EMPTY" }.any { slot ->
        val si = Items[slot]
        if (si.props.slots.isNotEmpty()) {
            si.props.slots.asSequence().flatMap { it.props.filters.asSequence() }.flatMap { it.filter.asSequence() }.contains(item.id)
        } else {
            false
        }
    }
}

fun goesIntoWeapon(weapon: TestItemTemplatesData, item: TestItemTemplatesData): Boolean {
    return weapon.props.slots.asSequence().flatMap { it.props.filters.asSequence() }.flatMap { it.filter.asSequence() }.contains(item.id)
}

fun prettyPrintBuilds(weapon: TestItemTemplatesData) {
    val validDrafts = validDraftsReader(weapon)
    val printer = finalBuildsPrinter(weapon)
    validDrafts.forEach { draft ->
        val build = WeaponBuild(weapon, draft.split(','))
        val recoil = build.totalRecoil().toString()
        val ergo = build.totalErgo().toString()
        val mods = build.modsNames()
        printer.writeLine(mutableListOf(recoil, ergo).also { it.addAll(mods) })
    }
    printer.close()
}

data class WeaponBuild(
    private val weapon: TestItemTemplatesData,
    private val slots: Collection<String>
) {

    fun weapon() = weapon

    fun mods() = slots.filter { it != "EMPTY" }.map { Items[it] }

    fun slots() = slots

    fun modsErgo() = mods().map { it.props.ergonomics }.sum()

    fun modsRecoil() = mods().map { it.props.recoil }.sum()

    fun totalErgo() = (weapon.props.ergonomics + modsErgo()).roundToInt()

    fun totalRecoil() = (weapon.props.recoilForceUp * (1 + (modsRecoil() / 100))).roundToInt()

    fun modsNames() = mods().map { Locale.itemName(it.id) }

}

fun isCompleteStock(stock: Stock): Boolean {
    val noSlots = stock.items.all { Items[it].props.slots.isEmpty() }

    if (noSlots) {
        return true
    }

    return stock.items.any { Items[it].props.slots.isEmpty() }
}

fun buildsStocks(stocks: MutableList<Stock>, root: ItemTree) {
    for (child in root.children) {
        if (child.type == ITEM && haveParentNamed(Items[child.id], "Stock")) {
            stocks.add(Stock(allParentStocks(child) + child.id))
        }
        buildsStocks(stocks, child)
    }
}

fun allParentStocks(child: ItemTree): List<String> {
    val stocks: MutableList<String> = mutableListOf()
    var current = child.parentTree
    while (current != null) {
        if (current.type == ITEM && haveParentNamed(Items[current.id], "Stock")) {
            stocks.add(current.id)
        }
        current = current.parentTree
    }
    return stocks
}

data class Stock(val items: List<String>) {

    fun names(): List<String> {
        return items.map { Items[it] }.map { it.name }
    }

}

private fun stocks(tree: ItemTree): List<Stock> {
    val stocks: MutableList<Stock> = mutableListOf()
    buildsStocks(stocks, tree)
    val completeStocks = stocks.filter { isCompleteStock(it) }
    println(stringBuilder(completeStocks.map { it.names() }))
    return completeStocks
}

data class Slot(val items: Set<String>) {
    fun names(): List<String> {
        return items.map { Items[it] }.map { it.name }
    }
}

fun foregrips(tree: ItemTree): List<Foregrip> {
    val grips: MutableList<Foregrip> = mutableListOf()
    buildsForegrips(grips, tree)
    println(stringBuilder(grips.map { it.names() }))
    return grips
}

fun buildsForegrips(grips: MutableList<Foregrip>, root: ItemTree) {
    for (child in root.children) {
        if (child.type == ITEM && haveParentNamed(Items[child.id], "Foregrip")) {
            grips.add(Foregrip(allParentMounts(child) + child.id))
        }
        buildsForegrips(grips, child)
    }
}

fun allParentMounts(child: ItemTree): List<String> {
    val grips: MutableList<String> = mutableListOf()
    var current = child.parentTree
    while (current != null) {
        if (current.type == ITEM && haveParentNamed(Items[current.id], "Mount")) {
            grips.add(current.id)
        }
        current = current.parentTree
    }
    return grips
}

data class Foregrip(val items: List<String>) {
    fun names(): List<String> {
        return items.map { Items[it] }.map { it.name }
    }
}

fun handguards(tree: ItemTree): List<Slot> {
    val guards: MutableList<String> = mutableListOf()
    buildsHandguards(guards, tree)
    return guards.distinct().map { Slot(setOf(it)) }
}

fun buildsHandguards(guard: MutableList<String>, root: ItemTree) {
    for (child in root.children) {
        if (child.type == ITEM && haveParentNamed(Items[child.id], "Handguard")) {
            guard.add(child.id)
        }
        buildsHandguards(guard, child)
    }
}

data class Muzzle(val items: List<String>) {

    fun names(): List<String> {
        return items.map { Items[it] }.map { it.name }
    }

}

fun muzzles(tree: ItemTree): List<Muzzle> {
    val muzzles: MutableList<Muzzle> = mutableListOf()
    buildsMuzzles(muzzles, tree)
    val completeStocks = muzzles.filter { isCompleteMuzzle(it) }
    println(stringBuilder(completeStocks.map { it.names() }))
    return completeStocks
}

fun buildsMuzzles(stocks: MutableList<Muzzle>, root: ItemTree) {
    for (child in root.children) {
        if (child.type == ITEM && haveParentNamed(Items[child.id], "Muzzle")) {
            stocks.add(Muzzle(allParentMuzzles(child) + child.id))
        }
        buildsMuzzles(stocks, child)
    }
}

fun isCompleteMuzzle(muzzle: Muzzle): Boolean {
    val noSlots = muzzle.items.all { Items[it].props.slots.isEmpty() }

    if (noSlots) {
        return true
    }

    return muzzle.items.any { Items[it].props.slots.isEmpty() }
}

fun allParentMuzzles(child: ItemTree): List<String> {
    val stocks: MutableList<String> = mutableListOf()
    var current = child.parentTree
    while (current != null) {
        if (current.type == ITEM && haveParentNamed(Items[current.id], "Muzzle")) {
            stocks.add(current.id)
        }
        current = current.parentTree
    }
    return stocks
}

fun weaponBuilds(weapon: TestItemTemplatesData) {
    val resultWriter = draftPrinter(weapon)
    val tree = itemTree(weapon)
    println(stringBuilder(tree))
    val completeStocks = stocks(tree)
    val completeForegrips = foregrips(tree)
    val completeHandguards = handguards(tree)
    val completeMuzzles = muzzles(tree)
    val transform = transform(tree)
    println(stringBuilder(transform))
    transform.values.forEach { set ->
        set.removeIf { it != "EMPTY" && haveParentNamed(Items[it], "Stock") }
        set.removeIf { it != "EMPTY" && haveParentNamed(Items[it], listOf("Mount", "Foregrip")) }
        set.removeIf { it != "EMPTY" && haveParentNamed(Items[it], listOf("Handguard")) }
        set.removeIf { it != "EMPTY" && haveParentNamed(Items[it], listOf("Muzzle")) }
    }
    val values = transform.values.toList()
        .distinct()
        .filter { it.isNotEmpty() }
        .filter { ! (it.size == 1 && it.contains("EMPTY")) }
        .toMutableList()
    val list = values.map { v -> v.map { i -> Slot(setOf(i)) } }.toMutableList()
    list.add(completeStocks.map { Slot(it.items.toSet()) } + Slot(setOf("EMPTY")))
    list.add(completeForegrips.map { Slot(it.items.toSet()) } + Slot(setOf("EMPTY")))
    list.add(completeHandguards.map { Slot(it.items.toSet()) } + Slot(setOf("EMPTY")))
    list.add(completeMuzzles.map { Slot(it.items.toSet()) } + Slot(setOf("EMPTY")))
    list.forEach { l ->
        l.forEach { s ->
            val build = WeaponBuild(weapon, s.items)
            println("${build.modsRecoil()} - ${build.modsNames()}")
        }
    }

//    println(stringBuilder(permutations(resultWriter, list)))
//    resultWriter.close()
//    println(draftReader(weapon).count())
//    val buildsPrinter = buildsPrinter(weapon)
//    val total = draftReader(weapon).count()
//    println("total = $total")
//    var progress = 0
//    draftReader(weapon).asSequence().withIndex().forEach {
//        val newProgress = ((it.index.toDouble() / total.toDouble()) * 100).toInt()
//        if (newProgress > progress) {
//            progress = newProgress
//            println("${LocalDateTime.now()} - $progress")
//        }
//        val slots = it.value.split(',')
//        if (isValidBuild(weapon, slots)) {
//            buildsPrinter.writeLine(slots)
//        }
//    }
//    buildsPrinter.close()
//    prettyPrintBuilds(weapon)
}
