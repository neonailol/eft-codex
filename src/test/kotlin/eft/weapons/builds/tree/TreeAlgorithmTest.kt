package eft.weapons.builds.tree

import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.stringBuilder
import org.testng.annotations.Test

class TreeAlgorithmTest {

    @Test
    fun `can make aks-74un weapon tree`() {
        val weapon = Items["583990e32459771419544dd2"]
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
    }

}