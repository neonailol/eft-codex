package eft.weapons.builds.exports.web

import eft.weapons.builds.tree.ItemTree
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.itemCost

fun processTree(tree: ItemTree): WebWeaponTree {
    val item = Items[tree.id]
    return WebWeaponTree(
        id = tree.id,
        name = itemName(tree.id),
        cost = generateItemCost(tree),
        recoil = item.props.recoilForceUp,
        ergonomics = item.props.ergonomics,
        slots = mainSlots(tree)
    )
}

fun mainSlots(tree: ItemTree): Set<WeaponSlot> {

    return emptySet()
}

private fun generateItemCost(tree: ItemTree) =
    itemCost(tree.id).map { ItemCost(it.trader, it.amount, it.currency, it.loyalLevel) }.toSet()
