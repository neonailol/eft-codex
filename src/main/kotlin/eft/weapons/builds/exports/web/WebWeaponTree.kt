package eft.weapons.builds.exports.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.utils.Traders

@JsonPropertyOrder("id", "name", "cost", "recoil", "ergonomics", "slots")
class WebWeaponTree(
    val id: String,
    val name: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val cost: Set<ItemCostOffer>,
    val recoil: Int,
    val ergonomics: Double,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val slots: Set<WeaponSlot>,
    val items: Set<WebItem>
)

@JsonPropertyOrder("type", "required", "mods", "slots")
data class WeaponSlot(
    val type: WeaponSlotType,
    val required: Boolean,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val mods: Set<WeaponMod>,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val slots: Set<WeaponSlot>
)

@JsonPropertyOrder("id", "type", "name", "shortName", "slots")
data class WeaponMod(
    val id: String,
    val type: WeaponModType,
    val name: String,
    val shortName: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val slots: Set<WeaponSlot>
)

@JsonPropertyOrder("id", "name", "shortName", "type", "recoilPercent", "ergonomics", "cost")
data class WebItem(
    val id: String,
    val name: String,
    val shortName: String,
    val type: WeaponModType,
    val recoilPercent: Double,
    val ergonomics: Double,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val cost: Set<ItemCostOffer>
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
    SLOT_GAS_BLOCK, SLOT_MUZZLE, SLOT_PISTOL_GRIP, SLOT_STOCK, SLOT_HANDGUARD, SLOT_MOUNT, SLOT_FOREGRIP, SLOT_BARREL, SLOT_RECEIVER, SLOT_SCOPE, SLOT_BIPOD, SLOT_TACTICAL
}

enum class WeaponModType {
    FOREGRIP, STOCK, MUZZLE, GAS_BLOCK, HANDGUARD, PISTOL_GRIP, MOUNT, BARREL, RECEIVER, AUXILIARY, BIPOD
}
