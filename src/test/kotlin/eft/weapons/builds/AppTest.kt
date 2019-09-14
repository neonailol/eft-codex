package eft.weapons.builds

import eft.weapons.builds.Locale.itemName
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import org.apache.commons.collections4.comparators.ComparatorChain
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.testng.annotations.Test
import kotlin.math.roundToInt

class AppTest {

    @Test
    fun `can load items`() {
        loadBytes("TestItemTemplates.bytes") as TestItemTemplates
    }

    @Test
    fun `can load locale`() {
        loadBytes("TestBackendLocaleRu.bytes") as TestBackendLocale
    }

    @Test
    fun `can find all pistols`() {
        val testItemTemplates = loadBytes("TestItemTemplates.bytes") as TestItemTemplates
        testItemTemplates.data.values.asSequence()
            .filter { it.parent == "5447b5cf4bdc2d65278b4567" }
            .sortedBy { itemName(it.id) }
            .forEach { println("${it.id} - ${itemName(it.id)}") }
    }

    @Test
    fun `list all parent types`() {
        val testItemTemplates = loadBytes("TestItemTemplates.bytes") as TestItemTemplates
        testItemTemplates.data.values.asSequence()
            .map { it.parent }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach {
                val parent = testItemTemplates.data.get(it)
                println("${parent?.id} - ${parent?.name}")
            }

        val first = testItemTemplates.data.values.first { it.parent == "" }
        println("Root: " + first.id)
    }

    @Test
    fun `build items hierarchy`() {
        val testItemTemplates = loadBytes("TestItemTemplates.bytes") as TestItemTemplates

        val root = testItemTemplates.data.values.asSequence().filter { it.parent == "" }.first()

        val parents = testItemTemplates.data.values.asSequence()
            .distinctBy { it.parent }
            .filter { it.parent != "" }
            .map { testItemTemplates.data[it.parent] !! }
            .toList()

        val tree = ItemCategories(root, children(testItemTemplates, root, parents))

        println(stringBuilder(tree))
    }

    @Test
    fun `can combine pm attachments`() {
        val testItemTemplates = loadBytes("TestItemTemplates.bytes") as TestItemTemplates
        val weapon = testItemTemplates.getItem("5448bd6b4bdc2dfc2f8b4569")
        val magazines = weapon.props.slots.asSequence()
            .filter { it.name == "mod_magazine" }
            .first().props.filters.asSequence().flatMap { it.filter.asSequence() }
            .map {
                testItemTemplates.data.values.first { f -> f.id == it }
            }.toList()

        println("Weapon: ${itemName(weapon.id)} Ergo: ${weapon.props.ergonomics}")
        magazines.forEach {
            println(
                "Weapon: ${itemName(weapon.id)} Mag: ${itemName(it.id)} Ergo: ${weapon.props.ergonomics + it.props.ergonomics}"
            )
        }
    }

    @Test
    fun `can list all tt attachments`() {
        val testItemTemplates = loadBytes("TestItemTemplates.bytes") as TestItemTemplates
        val weapon = testItemTemplates.getItem("571a12c42459771f627b58a0")
        val slotVariants = weapon.props.slots.map { SlotVariant(it) }.toMutableList()
        slotVariants.flatMap { it.toSlots() }
            .filter { it.id != "EMPTY" }
            .map { testItemTemplates.getItem(it.id) }
            .filter { it.props.slots.isNotEmpty() }
            .flatMap { it.props.slots }
            .map { SlotVariant(it) }
            .forEach {
                val found = slotVariants.find { v -> it.name == v.name }
                when {
                    found != null -> found.items.addAll(it.items)
                    else -> slotVariants.add(it)
                }
            }

        val slots = slotVariants.map { it.toSlots() }
        val variations = permutations(slots).filter { isValidBuild(testItemTemplates, weapon, it) }.toMutableList()
        val result: MutableCollection<List<String>> = mutableListOf()
        for (variation in variations) {
            val mods = variation.filter { it.id != "EMPTY" }.map { testItemTemplates.getItem(it.id) }
            val ergo = mods.map { it.props.ergonomics }.sum()
            val recoil = mods.map { it.props.recoil }.sum()
            val totalRecoil = (weapon.props.recoilForceUp * (1 + (recoil / 100))).roundToInt()
            val totalErgo = (weapon.props.ergonomics + ergo).roundToInt()
            val modsNames = mods.map { itemName(it.id) }
            val res = mutableListOf(totalErgo.toString(), totalRecoil.toString()).also { it.addAll(modsNames) }
            result.add(res)
        }
        val ergoComp: Comparator<List<String>> = Comparator { o1, o2 ->
            val a1 = o1[0]
            val a2 = o2[0]
            compareValues(a1, a2)
        }
        val recoilComp: Comparator<List<String>> = Comparator { o1, o2 ->
            val a1 = o1[1]
            val a2 = o2[1]
            compareValues(a2, a1)
        }
        val fc: Comparator<List<String>> = Comparator { o1, o2 ->
            for (i in 2 until o1.size) {
                if (o2.size > i) {
                    val compareValues = compareValues(
                        o1[i],
                        o2[i]
                    )
                    if (compareValues != 0) {
                        return@Comparator compareValues
                    }
                }
            }
            return@Comparator 0
        }
        val stringBuilder = StringBuilder()
        val csvPrinter = CSVPrinter(stringBuilder, CSVFormat.DEFAULT)
        csvPrinter.printRecord("Ergo", "Recoil", "Mods")
        csvPrinter.printRecords(result.sortedWith(ComparatorChain(listOf(fc))))
        println(stringBuilder.toString())
    }
}