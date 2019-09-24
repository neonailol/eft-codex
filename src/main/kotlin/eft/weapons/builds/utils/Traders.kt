package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders
import eft.weapons.builds.items.templates.TestTradersDataItems

object Traders {

    fun item(id: String): MutableList<TraderSellData> {
        val result: MutableList<TraderSellData> = mutableListOf()

        for (value in TradersData.values()) {
            val sellingItem = value.data.items.firstOrNull { it.tpl == id } ?: continue
            val loyalLevel = value.data.loyalLevelItems.getOrDefault(sellingItem.id, 0)
            result.add(TraderSellData(id, value, sellingItem, loyalLevel))
        }

        return result
    }

    private val mechanic = loadBytes("traders/mechanic.json") as TestTraders
    private val peacekeeper = loadBytes("traders/peacekeeper.json") as TestTraders
    private val prapor = loadBytes("traders/prapor.json") as TestTraders
    private val ragman = loadBytes("traders/ragman.json") as TestTraders
    private val skier = loadBytes("traders/skier.json") as TestTraders
    private val therapist = loadBytes("traders/therapist.json") as TestTraders

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