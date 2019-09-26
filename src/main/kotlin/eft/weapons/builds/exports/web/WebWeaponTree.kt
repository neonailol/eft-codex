package eft.weapons.builds.exports.web

import eft.weapons.builds.utils.Traders

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
    val type: WeaponModType,
    val recoilPercent: Double,
    val ergonomics: Double,
    val totalRecoilPercent: Double,
    val totalErgonomics: Double,
    val additionalMods: Set<WeaponMod>,
    val cost: Set<ItemCost>
)

data class ItemCost(
    val trader: Traders,
    val amount: Double,
    val currency: String,
    val loyalLevel: Int
)

enum class WeaponSlotType {
    GAS_BLOCK
}

enum class WeaponModType {
    FOREGRIP, STOCK, MUZZLE, GAS_BLOCK, HANDGUARD, PISTOL_GRIP
}
