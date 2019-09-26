package eft.weapons.builds.exports.web

import eft.weapons.builds.utils.ItemCost

class WebWeaponTree(
    val id: String,
    val name: String,
    val cost: Set<ItemCost>,
    val recoil: Double,
    val ergonomics: Double,
    val slots: Set<WeaponSlot>
)

data class WeaponSlot(
    val name: String,
    val required: Boolean,
    val type: WeaponSlotType,
    val slots: Set<WeaponSlot>,
    val mods: Set<WeaponMod>
)

data class WeaponMod(
    val id: String,
    val name: String,
    val shortName: String,
    val type: ModType,
    val recoilPercent: Double,
    val ergonomics: Double,
    val cost: Set<ItemCost>
)

enum class WeaponSlotType {
    GAS_BLOCK
}

enum class ModType {
    META, FOREGRIP, STOCK, MUZZLE, GAS_BLOCK, HANDGUARD
}
