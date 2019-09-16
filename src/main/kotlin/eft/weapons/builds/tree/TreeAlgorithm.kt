package eft.weapons.builds.tree

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
import eft.weapons.builds.utils.isMatters
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
    val children: List<ItemTree>
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
    )

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
    )
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

fun permutations(writer: ResultWriter, collections: List<Collection<String>>) {
    permutations(writer, collections.sortedBy { it.size }, 0, ArrayList())
}

fun permutations(
    writer: ResultWriter,
    origin: List<Collection<String>>,
    depth: Int,
    current: Collection<String>
) {
    if (depth == origin.size) {
        writer.writeLine(current)
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