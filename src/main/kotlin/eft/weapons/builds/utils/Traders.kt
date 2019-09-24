package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders
import eft.weapons.builds.items.templates.TestTradersDataItems

object Traders {

    fun item(id: String): MutableList<TraderSellData> {
        val result: MutableList<TraderSellData> = mutableListOf()

        for (value in TradersData.values()) {
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
        return item.joinToString { it.trader.name + " " + it.loyalLevel }
    }

}

enum class TradersData(name: String) {

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
    val trader: TradersData,
    val sellingItem: TestTradersDataItems,
    val loyalLevel: Int
) {

    override fun toString(): String = stringBuilder(this)
}