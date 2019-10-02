package eft.weapons.builds.exports.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.utils.Traders
import java.util.UUID

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

@JsonPropertyOrder("id", "type", "required", "mods", "slots")
data class WeaponSlot(
    val id: UUID = UUID.randomUUID(),
    val type: WeaponSlotType,
    val required: Boolean,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val mods: Set<WeaponMod>,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val slots: Set<WeaponSlot>
)

@JsonPropertyOrder("id", "itemId", "slots")
data class WeaponMod(
    val id: UUID = UUID.randomUUID(),
    val itemId: String,
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

@JsonPropertyOrder("id", "category", "name")
data class WebWeaponListEntry(
    val id: String,
    val category: String,
    val name: String
)