package eft.weapons.builds.tree

import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.printJson
import org.testng.annotations.Test

class TreeAlgorithmTest {

    @Test
    fun `can list all tt builds`() {
        val weapon = Items["571a12c42459771f627b58a0"]
        weaponBuilds(weapon)
    }

    @Test
    fun `can list all as val builds`() {
        val weapon = Items["57c44b372459772d2b39b8ce"]
        weaponBuilds(weapon)
    }

    @Test
    fun `can make all aks-74un builds`() {
        val weapon = Items["583990e32459771419544dd2"]
        weaponBuilds(weapon)
    }

    @Test
    fun `can make all ak-74n builds`() {
        val weapon = Items["5644bd2b4bdc2d3b4c8b4572"]
        weaponBuilds(weapon)
    }

    @Test
    fun `ergo`() {
        Items.all().filter {
            StrictMath.ceil(it.props.ergonomics) != it.props.ergonomics
        }.forEach {
            printJson(it)
        }
    }

}