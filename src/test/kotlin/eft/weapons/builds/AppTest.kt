package eft.weapons.builds

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.JsonNodeType.ARRAY
import com.fasterxml.jackson.databind.node.JsonNodeType.BINARY
import com.fasterxml.jackson.databind.node.JsonNodeType.BOOLEAN
import com.fasterxml.jackson.databind.node.JsonNodeType.MISSING
import com.fasterxml.jackson.databind.node.JsonNodeType.NULL
import com.fasterxml.jackson.databind.node.JsonNodeType.NUMBER
import com.fasterxml.jackson.databind.node.JsonNodeType.OBJECT
import com.fasterxml.jackson.databind.node.JsonNodeType.POJO
import com.fasterxml.jackson.databind.node.JsonNodeType.STRING
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ShortNode
import com.fasterxml.jackson.databind.node.TextNode
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.apache.commons.text.WordUtils
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
                val rootNode = context.addNode(Node("TestItemTemplates", it.key, it.value))
                if (it.value.isContainerNode) {
                    putIntoContext(context, rootNode, it)
                }
            }
        }
//        val pp = mapper.readValue(context.toString(), Any::class.java)
//        println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pp))
        codeGeneration(context)
    }

    private fun codeGeneration(context: Context) {
        context.nodes()
            .asSequence()
            .groupBy { it.prefix }
            .toSortedMap()
            .forEach { clazz, props ->
                val builder = StringBuilder()
                val clazzName =
                    className(clazz)

                builder.append("data class ${clazzName}(" + System.lineSeparator())
                props.forEachIndexed { index, node ->

                    val nodeType = if (node.typeString() == "Object") {
                        className(node.prefix)
                    } else {
                        node.typeString()
                    }

                    val postfix = when (index) {
                        props.size - 1 -> ""
                        else -> "," + System.lineSeparator()
                    }
                    builder.append("    val ${node.name}: ${nodeType}?" + postfix)
                }

                builder.append(System.lineSeparator() + ")" + System.lineSeparator())
                println(builder)
            }
    }

    private fun className(clazz: String): String {
        return clazz.split("#")
            .asSequence()
            .map { it.removePrefix("_") }
            .map { it.replace("_", " ") }
            .map { WordUtils.capitalize(it) }
            .map { it.replace(" ", "") }
            .joinToString("")
    }

    private fun putIntoContext(
        context: Context,
        rootNode: Node,
        entry: MutableMap.MutableEntry<String, JsonNode>
    ) {
        entry.value.fields().forEach {
            val node = context.addNode(Node(rootNode.prefix + "#" + rootNode.name, it.key, it.value))
            if (it.value.isContainerNode) {
                putIntoContext(context, node, it)
            }
        }
    }

}

class Context {
    private val nodes: MutableSet<Node> = mutableSetOf()

    fun addNode(node: Node): Node {
        if (node.prefix == "TestItemTemplates#_props" && node.name == "Buffs") {
            // Some weird stuff goes with buffs
            return node
        }
        val addded = nodes.add(node)
        if (addded.not()) {
            nodes.filter { it.prefix == node.prefix && it.name == node.name }
                .forEach {
                    it.updateType(node)
                }
        }
        return node
    }

    fun nodes(): Set<Node> {
        return HashSet(nodes)
    }

    override fun toString(): String {
        return ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("nodes", nodes)
            .toString()
    }

}

data class Node(
    val prefix: String,
    val name: String,
    var type: JsonNode
) {

    fun updateType(node: Node) {
        if (type.javaClass != node.type.javaClass) {
            if (type.javaClass == IntNode::class.java && node.type.javaClass == DoubleNode::class.java) {
                type = node.type
                return
            }
            if (type.javaClass == DoubleNode::class.java && node.type.javaClass == IntNode::class.java) {
                return
            }
            if (type.javaClass == ArrayNode::class.java && node.type.javaClass == NullNode::class.java) {
                return
            }
            if (type.javaClass == IntNode::class.java && node.type.javaClass == TextNode::class.java) {
                type = node.type
                return
            }
            throw RuntimeException("Unknown upgrade path from ${type.javaClass} to ${node.type.javaClass}")
        }
    }

    fun typeString(): String {
        return when (type.nodeType) {
            ARRAY -> "Collection"
            BINARY -> "binary"
            BOOLEAN -> "Boolean"
            MISSING -> "missing"
            NULL -> "null"

            NUMBER -> {
                return when (type) {
                    is ShortNode -> "Short"
                    is IntNode -> "Int"
                    is LongNode -> "Long"
                    is BigIntegerNode -> "BigInteger"
                    is FloatNode -> "Float"
                    is DoubleNode -> "Double"
                    is DecimalNode -> "Decimal"
                    else -> throw RuntimeException("Unknown number node type")
                }
            }

            OBJECT -> "Object"
            POJO -> "pojo"
            STRING -> "String"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        if (name != other.name) return false
        if (prefix != other.prefix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = prefix.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("prefix", prefix)
            .append("name", name)
            .append("type", typeString())
            .toString()
    }
}