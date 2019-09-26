package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders
import eft.weapons.builds.items.templates.TestTradersDataItems

object TradersInfo {

    fun item(id: String): MutableList<TraderSellData> {
        val result: MutableList<TraderSellData> = mutableListOf()

        for (value in Traders.values()) {
            val sellingItem = value.data.items.firstOrNull { it.tpl == id } ?: continue
            if (sellingItem.parentId != "hideout") {
                continue
            }
            val loyalLevel = value.data.loyalLevelItems.getOrDefault(sellingItem.id, 0)
            result.add(TraderSellData(id, value, sellingItem, loyalLevel))
        }

        return result
    }

    fun itemString(id: String): String {
        val item = item(id)
        return item.joinToString { "${it.trader.name} ${it.loyalLevel}" }
    }

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
                    .mapTo(result) { ItemCost(it.count, Locale.itemName(it.tpl), value, loyalLevel) }
        }

        return result
    }

}

data class ItemCost(
        val amount: Double,
        val currency: String,
        val trader: Traders,
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

    private val content: TestTraders = loadBytes("traders/$name.json") as TestTraders
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