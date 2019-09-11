package eft.weapons.builds

import kotlin.reflect.full.memberProperties
import kotlin.test.Test

class AppTest {

    @Test
    fun `can load some json`() {
        val mapper = mapper()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        var testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
    }

    @Test
    fun `can find all weapons`() {
        val mapper = mapper()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data.values.asSequence()
            .filter { it._parent == "5447b5cf4bdc2d65278b4567" }
            .forEach { println(it) }
    }

    @Test
    fun `can list pm attachments`() {
        val mapper = mapper()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        val weapon = testItemTemplates.data.values.asSequence()
            .filter { it._id == "5448bd6b4bdc2dfc2f8b4569" }
            .first()
        val magazines = weapon._props?.Slots?.asSequence()
            ?.filter { it._name == "mod_magazine" }
            ?.first()?._props?.filters?.asSequence()?.flatMap { it.Filter !!.asSequence() } !!
            .map {
                testItemTemplates.data.values.asSequence()
                    .filter { f -> f._id == it }
                    .first()
            }.toList()

        println("Weapon: ${weapon._name} Ergo: ${weapon._props?.Ergonomics}")
        magazines.forEach {
            println("Weapon: ${weapon._name} Mag: ${it._name} Ergo: ${weapon._props?.Ergonomics !! + it._props?.Ergonomics !!}")
        }

    }

    @Test
    fun `can find all params of weapons`() {
        val mapper = mapper()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        var testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        val validProps = mutableSetOf<String>()
        val invalidProps = mutableSetOf<String>()
        testItemTemplates.data.values.asSequence()
            .filter { it._parent == "5447b6254bdc2dc3278b4568" }
            .forEach {
                for (memberProperty in TestItemTemplatesItemProps::class.memberProperties) {
                    if (memberProperty.get(it?._props !!) != null) {
                        validProps.add(memberProperty.name)
                    } else {
                        invalidProps.add(memberProperty.name)
                    }
                }
            }
        println(validProps)
        println(invalidProps)
    }

    @Test
    fun `list all parent types`() {
        val mapper = mapper()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data.values.asSequence()
            .map { it._parent !! }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach {
                val parent = testItemTemplates.data[it]
                println(parent?._name)
            }

        val first = testItemTemplates.data.values.first { it._parent == "" }
        println("Root: " + first._id)
    }

    @Test
    fun `build items hierarchy`() {
        val mapper = mapper()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        val testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
        testItemTemplates.data.values.asSequence().filter { it._parent == null }.forEach {
            println("---" + it._name)
        }
        val parents = testItemTemplates.data.values.asSequence()
            .distinctBy { it._parent }
            .toList()
//        println(parents.map { it._name })
    }

}