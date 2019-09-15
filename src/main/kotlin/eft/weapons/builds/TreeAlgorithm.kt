package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.ItemTreeNodeType.ITEM
import eft.weapons.builds.ItemTreeNodeType.META
import eft.weapons.builds.ItemTreeNodeType.ROOT
import eft.weapons.builds.Locale.itemName
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlots
import eft.weapons.builds.items.templates.TestItemTemplatesDataPropsSlotsPropsFilters

fun itemTree(weapon: TestItemTemplatesData): ItemTree {
    return ItemTree(weapon, ROOT, true, children(weapon))
}

fun children(item: TestItemTemplatesData): List<ItemTree> {
    return item.props.slots
        .filter { it.props.filters.isNotEmpty() }
        .map { it to children(it.props.filters.first()) }
        .filter { it.second.isNotEmpty() }
        .map { ItemTree(it.first, META, it.first.required, it.second) }
}

fun children(filter: TestItemTemplatesDataPropsSlotsPropsFilters): List<ItemTree> {
    return filter.filter
        .filter { isMatters(it) }
        .map { Items[it] }
        .map { ItemTree(it, ITEM, false, children(it)) }
}

@JsonPropertyOrder(value = ["id", "name", "type", "required", "children"])
class ItemTree(
    val id: String,
    val name: String,
    val type: ItemTreeNodeType,
    val required: Boolean,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemTree>
) {

    constructor(
        item: TestItemTemplatesData,
        type: ItemTreeNodeType,
        required: Boolean,
        children: List<ItemTree>
    ) : this(
        item.id,
        itemName(item.id),
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
        "EMPTY",
        item.name,
        type,
        required,
        children
    )
}

enum class ItemTreeNodeType { ROOT, META, ITEM }
