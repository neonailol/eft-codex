package eft.weapons.builds.exports.web

import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.tree.ItemTree
import eft.weapons.builds.tree.ItemTreeNodeType
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.Locale.itemShortName
import eft.weapons.builds.utils.haveParentNamed
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
    return tree.children.map { weaponSlotMapping(it) }.toSet()
}

private fun weaponSlotMapping(it: ItemTree): WeaponSlot {
    return WeaponSlot(
        type = slotType(it),
        required = it.required,
        slots = childrenSlots(it),
        mods = childrenMods(it)
    )
}

fun childrenMods(tree: ItemTree): Set<WeaponMod> {
    return tree.children.filter { it.type == ItemTreeNodeType.ITEM }.map {
        val item = Items[it.id]
        WeaponMod(
            id = it.id,
            name = itemName(it.id),
            shortName = itemShortName(it.id),
            type = weaponModType(item),
            recoilPercent = item.props.recoil,
            ergonomics = item.props.ergonomics,
            totalRecoilPercent = 0.0,
            totalErgonomics = 0.0,
            additionalMods = setOf(),
            cost = generateItemCost(it),
            slots = emptySet()
        )
    }.toSet()
}

fun weaponModType(mod: TestItemTemplatesData): WeaponModType {
    if (haveParentNamed(mod, "Gasblock")) {
        return WeaponModType.GAS_BLOCK
    }
    if (haveParentNamed(mod, "Muzzle")) {
        return WeaponModType.MUZZLE
    }
    if (haveParentNamed(mod, "PistolGrip")) {
        return WeaponModType.PISTOL_GRIP
    }
    if (haveParentNamed(mod, "Stock")) {
        return WeaponModType.STOCK
    }
    throw RuntimeException("Unknown mapping for mod type: ${mod.name}")
}

fun childrenSlots(tree: ItemTree): Set<WeaponSlot> {
    return tree.children.filter { it.type == ItemTreeNodeType.META }.map { weaponSlotMapping(it) }.toSet()
}

fun slotType(slot: ItemTree): WeaponSlotType {
    return when (slot.name) {
        "mod_gas_block" -> WeaponSlotType.SLOT_GAS_BLOCK
        "mod_muzzle" -> WeaponSlotType.SLOT_MUZZLE
        "mod_pistol_grip" -> WeaponSlotType.SLOT_PISTOL_GRIP
        "mod_stock" -> WeaponSlotType.SLOT_STOCK
        else -> throw RuntimeException("Unknown mapping for slot type: ${slot.name}")
    }
}

private fun generateItemCost(tree: ItemTree) =
    itemCost(tree.id).map { ItemCost(it.trader, it.amount, it.currency, it.loyalLevel) }.toSet()
