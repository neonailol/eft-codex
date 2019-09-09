package eft.weapons.builds

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.Test

class AppTest {

    @Test
    fun `can load some json`() {
        val mapper = ObjectMapper().findAndRegisterModules()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        var testItemTemplates = mapper.readValue(json, TestItemTemplates::class.java)
    }

    @Test
    fun `dynamic json schema detection`() {
        val mapper = ObjectMapper().findAndRegisterModules()
        val json = this.javaClass.getResourceAsStream("/TestItemTemplates.json")
        val tree = mapper.readTree(json)
        val data = tree.get("data")
        val context = Context()
        val types = HashSet<String>()
        for (datum in data) {
            datum.fields().forEach {
                context.addNode(Node(it.key, it.value))
            }
        }
        println(context)
    }

}

class Context {
    private val nodes: MutableSet<Node> = mutableSetOf()

    fun addNode(node: Node) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "Context(nodes=$nodes)"
    }

}

data class Node(
    val name: String,
    val type: JsonNode
) {

    override fun toString(): String {
        return "Node(name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int = name.hashCode()

}