package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders

fun itemCost(id: String): List<TraderSellData> {
    val result: MutableList<TraderSellData> = mutableListOf()

    for (value in Traders.values()) {
        val sellingItem = value.data.items.firstOrNull { it.tpl == id } ?: continue
        if (sellingItem.parentId != "hideout") {
            continue
        }
        val loyalLevel = value.data.loyalLevelItems.getOrDefault(sellingItem.id, 0)
        val barter = value.data.barterScheme[sellingItem.id] ?: continue
        for (barterItem in barter) {
            val traderSellDataBarter: MutableSet<TraderSellDataBarter> = mutableSetOf()
            for (bi in barterItem) {
                traderSellDataBarter.add(TraderSellDataBarter(bi.count, Locale.itemName(bi.tpl)))
            }
            result.add(TraderSellData(value, loyalLevel, traderSellDataBarter))
        }
    }

    return result
}

fun itemCostString(id: String): String {
    return itemCost(id).joinToString { "${it.trader} - ${it.loyalLevel}" }
}

data class TraderSellData(
    val trader: Traders,
    val loyalLevel: Int,
    val barter: Set<TraderSellDataBarter>
) {

    override fun toString(): String = stringBuilder(this)
}

data class TraderSellDataBarter(
    val amount: Double,
    val currency: String
)

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
