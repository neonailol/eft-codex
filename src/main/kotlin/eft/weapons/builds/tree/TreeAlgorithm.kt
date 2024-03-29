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
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.haveParentNamed
import eft.weapons.builds.utils.isMatters
import eft.weapons.builds.utils.itemCostString
import eft.weapons.builds.utils.stringBuilder
import java.util.TreeSet
import kotlin.collections.set

fun itemTree(weapon: TestItemTemplatesData): ItemTree {
    return ItemTree(weapon, "", ROOT, true, children(weapon), itemCostString(weapon.id))
}

fun children(item: TestItemTemplatesData): List<ItemTree> {
    return item.props.slots
        .filter { it.props.filters.isNotEmpty() }
        .map { it to children(it) }
        .filter { it.second.isNotEmpty() }
        .map { ItemTree(it.first, META, it.first.required, it.second, itemCostString(it.first.id)) }
}

fun children(filter: TestItemTemplatesDataPropsSlots): List<ItemTree> {
    return filter.props.filters.first().filter
        .filter { isMatters(it) }
        .map { Items[it] }
        .map { ItemTree(it, filter.id, ITEM, false, children(it), itemCostString(it.id)) }
}

@JsonPropertyOrder(value = ["id", "name", "parent", "type", "required", "trader", "mounts", "children"])
data class ItemTree(
    val id: String,
    val name: String,
    val parent: String,
    val type: ItemTreeNodeType,
    val required: Boolean,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemTree>,
    @JsonIgnore
    var parentTree: ItemTree? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val trader: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var mounts: Set<String> = emptySet(),
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val conflicts: Set<String> = emptySet()
) {

    constructor(
        item: TestItemTemplatesData,
        parent: String,
        type: ItemTreeNodeType,
        required: Boolean,
        children: List<ItemTree>,
        trader: String
    ) : this(
        item.id,
        itemName(item.id),
        parent,
        type,
        required,
        children,
        null,
        trader,
        item.props.conflictingItems.toSet()
    ) {
        children.forEach {
            it.parentTree = this
            it.mounts = allParentMounts(it)
        }
    }

    constructor(
        item: TestItemTemplatesDataPropsSlots,
        type: ItemTreeNodeType,
        required: Boolean,
        children: List<ItemTree>,
        trader: String
    ) : this(
        item.id,
        item.name,
        item.parent,
        type,
        required,
        children,
        null,
        trader
    ) {
        children.forEach {
            it.parentTree = this
            it.mounts = allParentMounts(it)
        }
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

    fun totalErgo() = (weapon.props.ergonomics + modsErgo())

    fun totalRecoil() = (weapon.props.recoilForceUp * (1 + (modsRecoil() / 100)))

    fun modsNames() = mods().map { itemName(it.id) }

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

fun allParentStocks(child: ItemTree): Set<String> {
    val stocks: MutableSet<String> = mutableSetOf()
    var current = child.parentTree
    while (current != null) {
        if (current.type == ITEM && haveParentNamed(Items[current.id], "Stock")) {
            stocks.add(current.id)
        }
        current = current.parentTree
    }
    return stocks
}

private fun stocks(tree: ItemTree): List<Stock> {
    val stocks: MutableList<Stock> = mutableListOf()
    buildsStocks(stocks, tree)
    val completeStocks = stocks.filter { isCompleteStock(it) }
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

fun allParentMounts(child: ItemTree): Set<String> {
    val grips: MutableSet<String> = mutableSetOf()
    var current = child.parentTree
    while (current != null) {
        if (current.type == ITEM && haveParentNamed(Items[current.id], "Mount")) {
            grips.add(current.id)
        }
        current = current.parentTree
    }
    return grips
}

fun muzzles(tree: ItemTree): List<Muzzle> {
    val muzzles: MutableList<Muzzle> = mutableListOf()
    buildsMuzzles(muzzles, tree)
    return muzzles.filter { isCompleteMuzzle(it) }
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

fun allParentMuzzles(child: ItemTree): Set<String> {
    val muzzles: MutableSet<String> = mutableSetOf()
    var current = child.parentTree
    while (current != null) {
        if (current.type == ITEM && haveParentNamed(Items[current.id], "Muzzle")) {
            muzzles.add(current.id)
        }
        current = current.parentTree
    }
    return muzzles
}

open class CompositeAttachment(val items: Set<String>) {

    fun names(): List<String> {
        return items.map { Items[it] }.map { it.name }
    }

    override fun toString(): String = stringBuilder(this)
}

class Muzzle(items: Set<String>) : CompositeAttachment(items)

class Stock(items: Set<String>) : CompositeAttachment(items)

class Foregrip(items: Set<String>) : CompositeAttachment(items)

fun weaponBuilds(weapon: TestItemTemplatesData): ItemTree {
    val tree = itemTree(weapon)
//    println(stringBuilder(tree))
    val completeStocks = stocks(tree)
//    println(stringBuilder(completeStocks.map { it.names() }))
    val completeForegrips = foregrips(tree)
//    println(stringBuilder(completeForegrips.map { it.names() }))
    val completeMuzzles = muzzles(tree)
//    println(stringBuilder(completeMuzzles.map { it.names() }))
    return tree
}
