package eft.weapons.builds.exports.web

import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.tree.ItemTree
import eft.weapons.builds.tree.ItemTreeNodeType
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.Locale.itemShortName
import eft.weapons.builds.utils.TraderSellData
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
        slots = mainSlots(tree),
        items = items(tree)
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
            itemId = it.id,
            slots = mainSlots(it)
        )
    }.toSet()
}

fun items(tree: ItemTree): Set<WebItem> {

    val items: MutableSet<WebItem> = mutableSetOf()
    for (child in tree.children) {
        items.addAll(items(child))
    }

    return items + tree.children.filter { it.type == ItemTreeNodeType.ITEM }.map {
        val item = Items[it.id]
        WebItem(
            id = it.id,
            name = itemName(it.id),
            shortName = itemShortName(it.id),
            type = weaponModType(item),
            recoilPercent = item.props.recoil,
            ergonomics = item.props.ergonomics,
            cost = generateItemCost(it)
        )
    }.sortedBy { it.type }.toSet()
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
    if (haveParentNamed(mod, "Handguard")) {
        return WeaponModType.HANDGUARD
    }
    if (haveParentNamed(mod, "Mount")) {
        return WeaponModType.MOUNT
    }
    if (haveParentNamed(mod, "Foregrip")) {
        return WeaponModType.FOREGRIP
    }
    if (haveParentNamed(mod, "Barrel")) {
        return WeaponModType.BARREL
    }
    if (haveParentNamed(mod, "Receiver")) {
        return WeaponModType.RECEIVER
    }
    if (haveParentNamed(mod, "AuxiliaryMod")) {
        return WeaponModType.AUXILIARY
    }
    if (haveParentNamed(mod, "Bipod")) {
        return WeaponModType.BIPOD
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
        "mod_pistol_grip_akms" -> WeaponSlotType.SLOT_PISTOL_GRIP
        "mod_pistolgrip" -> WeaponSlotType.SLOT_PISTOL_GRIP
        "mod_stock" -> WeaponSlotType.SLOT_STOCK
        "mod_stock_000" -> WeaponSlotType.SLOT_STOCK
        "mod_stock_001" -> WeaponSlotType.SLOT_STOCK
        "mod_stock_akms" -> WeaponSlotType.SLOT_STOCK
        "mod_handguard" -> WeaponSlotType.SLOT_HANDGUARD
        "mod_mount" -> WeaponSlotType.SLOT_MOUNT
        "mod_mount_000" -> WeaponSlotType.SLOT_MOUNT
        "mod_mount_001" -> WeaponSlotType.SLOT_MOUNT
        "mod_mount_002" -> WeaponSlotType.SLOT_MOUNT
        "mod_mount_003" -> WeaponSlotType.SLOT_MOUNT
        "mod_foregrip" -> WeaponSlotType.SLOT_FOREGRIP
        "mod_barrel" -> WeaponSlotType.SLOT_BARREL
        "mod_reciever" -> WeaponSlotType.SLOT_RECEIVER
        "mod_scope" -> WeaponSlotType.SLOT_SCOPE
        "mod_bipod" -> WeaponSlotType.SLOT_BIPOD
        "mod_tactical" -> WeaponSlotType.SLOT_TACTICAL
        else -> throw RuntimeException("Unknown mapping for slot type: ${slot.name}")
    }
}

private fun generateItemCost(tree: ItemTree) = itemCost(tree.id).map { itemCostOffer(it) }.toSet()

private fun itemCostOffer(it: TraderSellData) = ItemCostOffer(it.trader, it.loyalLevel, barter(it))

private fun barter(it: TraderSellData) = it.barter.map { ic -> ItemCost(ic.amount, ic.currency) }.toSet()
