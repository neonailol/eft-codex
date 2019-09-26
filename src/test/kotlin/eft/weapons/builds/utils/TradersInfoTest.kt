package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders
import org.testng.annotations.Test

class TradersInfoTest {

    @Test
    fun `can load traders`() {
        loadBytes("traders/mechanic.json") as TestTraders
        loadBytes("traders/peacekeeper.json") as TestTraders
        loadBytes("traders/prapor.json") as TestTraders
        loadBytes("traders/ragman.json") as TestTraders
        loadBytes("traders/skier.json") as TestTraders
        loadBytes("traders/therapist.json") as TestTraders
    }

    @Test
    fun `can find item cost`() {
        printJson(itemCost("5644bd2b4bdc2d3b4c8b4572"))
    }

}