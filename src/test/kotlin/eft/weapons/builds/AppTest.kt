package eft.weapons.builds

import eft.weapons.builds.Locale.itemName
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import org.testng.annotations.Test

class AppTest {

    @Test
    fun `can load items`() {
        loadBytes("items.json") as TestItemTemplates
    }

    @Test
    fun `can load locale`() {
        loadBytes("locale.json") as TestBackendLocale
    }

    @Test
    fun `can find all pistols`() {
        Items.children("5447b5cf4bdc2d65278b4567")
            .sortedBy { itemName(it.id) }
            .forEach { println("${it.id} - ${itemName(it.id)}") }
    }

    @Test
    fun `can find all assault carbines`() {
        Items.children("5447b5fc4bdc2d87278b4567")
            .sortedBy { itemName(it.id) }
            .forEach { println("${it.id} - ${itemName(it.id)}") }
    }

    @Test
    fun `list all parent types`() {
        Items.all().asSequence()
            .map { it.parent }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach {
                val parent = Items[it]
                println("${parent.id} - ${parent.name}")
            }
        val root = Items.all().first { it.parent == "" }
        println("${root.id} - Root")
    }

    @Test
    fun `build items hierarchy`() {
        val root = Items.all().asSequence().filter { it.parent == "" }.first()
        val parents = Items.all().asSequence()
            .distinctBy { it.parent }
            .filter { it.parent != "" }
            .map { Items[it.parent] }
            .toList()
        val tree = ItemCategories(root, children(root, parents))
        println(stringBuilder(tree))
    }

    @Test
    fun `can list all tt builds`() {
        val weapon = Items["571a12c42459771f627b58a0"]
        val builds = weaponBuilds(weapon)
        printBuilds(weapon, builds)
    }

    @Test
    fun `can list all as val builds`() {
        val weapon = Items["57c44b372459772d2b39b8ce"]
        val builds = weaponBuilds(weapon)
        printBuilds(weapon, builds)
    }

    @Test
    fun `can list all aks-74un builds`() {
        val weapon = Items["583990e32459771419544dd2"]
        val builds = weaponBuilds(weapon)
        printBuilds(weapon, builds)
    }

}