package eft.weapons.builds.tree

import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.stringBuilder
import org.testng.annotations.Test

class TreeAlgorithmTest {

    @Test
    fun `can list all tt builds`() {
        val weapon = Items["571a12c42459771f627b58a0"]
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
        val filter = tree.children.filter { it.required }
        println(stringBuilder(filter))
    }

    @Test
    fun `can list all as val builds`() {
        val weapon = Items["57c44b372459772d2b39b8ce"]
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
    }

    @Test
    fun `can list all aks-74un builds`() {
        val weapon = Items["583990e32459771419544dd2"]
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
        println(stringBuilder(transform(tree)))
    }
}