package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestTraders
import org.testng.annotations.Test

class TradersTest {

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
    fun `can find item from trader`() {
        println(Traders.item("590c678286f77426c9660122"))
    }

}