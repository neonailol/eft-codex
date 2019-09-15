package eft.weapons.builds

import eft.weapons.builds.categories.ItemCategories
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.Locale.itemName
import eft.weapons.builds.utils.loadBytes
import eft.weapons.builds.utils.stringBuilder
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
        val tree = ItemCategories(root, eft.weapons.builds.categories.children(root, parents))
        println(stringBuilder(tree))
    }

}
