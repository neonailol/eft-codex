package eft.weapons.builds.tree

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlots
import eft.weapons.builds.tree.ItemTreeNodeType.ITEM
import eft.weapons.builds.tree.ItemTreeNodeType.META
import eft.weapons.builds.tree.ItemTreeNodeType.ROOT
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.isMatters
import java.util.LinkedList

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
            val list = result.getOrDefault(child.id, HashSet())
            list.addAll(child.children.map { it.id })
            result[child.id] = list
            processChildren(result, child.children)
        } else if (child.type == ITEM) {
            val list = result.getOrDefault(child.parent, HashSet())
            list.add(child.id)
            result[child.parent] = list
            processChildren(result, child.children)
        }
    }
}

fun permutations(collections: List<Collection<String>>): MutableCollection<Collection<String>> {
    if (collections.isNullOrEmpty()) {
        return ArrayList()
    }
    val res: MutableCollection<Collection<String>> = LinkedList()
    permutationsImpl(collections, res, 0, ArrayList())
    return res
}

fun permutationsImpl(
    origin: List<Collection<String>>,
    result: MutableCollection<Collection<String>>,
    depth: Int,
    current: Collection<String>
) {
    if (depth == origin.size) {
        result.add(current)
        return
    }

    val currentCollection = origin[depth]
    for (element in currentCollection) {
        permutationsImpl(origin, result, depth + 1, current + element)
    }
}
