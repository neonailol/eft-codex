package eft.weapons.builds.exports.web

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.utils.Traders

@JsonPropertyOrder("id", "name", "cost", "recoil", "ergonomics", "slots")
class WebWeaponTree(
    val id: String,
    val name: String,
    val cost: Set<ItemCostOffer>,
    val recoil: Int,
    val ergonomics: Double,
    val slots: Set<WeaponSlot>
)

@JsonPropertyOrder("type", "required", "mods", "slots")
data class WeaponSlot(
    val type: WeaponSlotType,
    val required: Boolean,
    val mods: Set<WeaponMod>,
    val slots: Set<WeaponSlot>
)

@JsonPropertyOrder(
    "id",
    "name",
    "shortName",
    "type",
    "recoilPercent",
    "ergonomics",
    "totalRecoilPercent",
    "totalErgonomics",
    "additionalMods",
    "cost",
    "slots"
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
    val cost: Set<ItemCostOffer>,
    val slots: Set<WeaponSlot>
)

@JsonPropertyOrder("trader", "loyaltyLevel", "barter")
data class ItemCostOffer(
    val trader: Traders,
    val loyalLevel: Int,
    val barter: Set<ItemCost>
)

@JsonPropertyOrder("amount", "currency")
data class ItemCost(
    val amount: Double,
    val currency: String
)

enum class WeaponSlotType {
    SLOT_GAS_BLOCK, SLOT_MUZZLE, SLOT_PISTOL_GRIP, SLOT_STOCK, SLOT_HANDGUARD, SLOT_MOUNT, SLOT_FOREGRIP
}

enum class WeaponModType {
    FOREGRIP, STOCK, MUZZLE, GAS_BLOCK, HANDGUARD, PISTOL_GRIP, MOUNT
}
