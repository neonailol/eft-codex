package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders
import eft.weapons.builds.items.templates.TestTradersDataItems

fun itemCost(id: String): List<ItemCost> {
    val result: MutableList<ItemCost> = mutableListOf()

    for (value in Traders.values()) {
        val sellingItem = value.data.items.firstOrNull { it.tpl == id } ?: continue
        if (sellingItem.parentId != "hideout") {
            continue
        }
        val loyalLevel = value.data.loyalLevelItems.getOrDefault(sellingItem.id, 0)
        val barter = value.data.barterScheme[sellingItem.id] ?: continue
        barter.asSequence()
            .flatMap { it.asSequence() }
            .mapTo(result) { ItemCost(value, it.count, Locale.itemName(it.tpl), loyalLevel) }
    }

    return result
}

fun itemCostString(id: String): String {
    return itemCost(id).joinToString { "${it.trader} - ${it.loyalLevel}" }
}

data class ItemCost(
    val trader: Traders,
    val amount: Double,
    val currency: String,
    val loyalLevel: Int
) {

    override fun toString(): String = stringBuilder(this)
}

enum class Traders(name: String) {

    MECHANIC("mechanic"),
    PEACEKEEPER("peacekeeper"),
    PRAPOR("prapor"),
    RAGMAN("ragman"),
    SKIER("skier"),
    THERAPIST("therapist");

    private var content: TestTraders = loadBytes("traders/$name.json") as TestTraders
    val data = content.data
}

class TraderSellData(
    val itemId: String,
    val trader: Traders,
    val sellingItem: TestTradersDataItems,
    val loyalLevel: Int
) {

    override fun toString(): String = stringBuilder(this)
}