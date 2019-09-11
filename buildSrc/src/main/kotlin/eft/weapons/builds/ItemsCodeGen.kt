package eft.weapons.builds

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ShortNode
import com.fasterxml.jackson.databind.node.TextNode
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.apache.commons.text.WordUtils
import org.gradle.api.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun mapper(): ObjectMapper {
    return ObjectMapper().findAndRegisterModules()
}

fun parseBytes(directory: File, project: Project) {
    cleanGenerated(directory)
    paresItemTemplates(project, directory)
}

private fun paresItemTemplates(project: Project, directory: File) {
    val mapper = mapper()
    val json = Files.readString(Paths.get(project.rootDir.absolutePath, "TextAsset", "TestItemTemplates.bytes"))
    val tree = mapper.readTree(json)
    val data = tree.get("data")
    val context = Context()
    val types = HashSet<String>()
    data.fields().forEach {
        val rootNode = context.addNode(Node("TestItemTemplatesItem", it.key, it.value))
        if (it.value.isContainerNode) {
            putIntoContext(context, rootNode, it)
        }
    }
    val codeGeneration = codeGeneration(context)
    val createFile = Files.createFile(Paths.get(directory.absolutePath, "TestItemTemplates.kt"))
    createFile.toFile().writeText(codeGeneration)
}

public fun codeGeneration(context: Context): String {
    val builder = StringBuilder()
    builder.append("package eft.weapons.builds.items.templates" + System.lineSeparator())
    builder.append("import com.fasterxml.jackson.annotation.JsonProperty" + System.lineSeparator())
    builder.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties" + System.lineSeparator())
    builder.append("import eft.weapons.builds.stringBuilder" + System.lineSeparator())
    builder.append(System.lineSeparator())
    context.nodes()
        .asSequence()
        .groupBy { it.prefix }
        .toSortedMap()
        .forEach { (clazz, props) ->
            val clazzName =
                className(clazz)

            if (clazzName == "TestItemTemplatesItemProps") {
                builder.append("@JsonIgnoreProperties(\"Buffs\")" + System.lineSeparator())
            }

            if (props.size == 1 && props[0].mapNode) {
                builder.append("class ${clazzName}Map : HashMap<String, ${clazzName}>() {" + System.lineSeparator())
                builder.append("    override fun toString(): String = stringBuilder(this)" + System.lineSeparator())
                builder.append("}" + System.lineSeparator() + System.lineSeparator())
                builder.append(System.lineSeparator())
            } else {
                builder.append("class ${clazzName} {" + System.lineSeparator())
                props.forEachIndexed { index, node ->

                    val nodeType = when (node.typeString()) {
                        "Object" -> className(node.prefix + "_" + node.name)
                        "Collection" -> "Collection<${collectionClass(node)}>"
                        else -> node.typeString()
                    }

                    val mapNode = when(node.mapNode) {
                        true -> "Map"
                        false -> ""
                    }
                    if (node.name != "Buffs") {
                        val postfix = when (index) {
                            props.size - 1 -> ""
                            else -> "" + System.lineSeparator()
                        }
                        if (nodeType == "Boolean") {
                            builder.append("    @JsonProperty(\"${node.name}\")" + System.lineSeparator())
                        }
                        builder.append("    var ${node.name}: ${nodeType}${mapNode}? = null" + postfix)
                    }
                }
                builder.append(System.lineSeparator())
                builder.append("    override fun toString(): String = stringBuilder(this)" + System.lineSeparator())
                builder.append("}" + System.lineSeparator() + System.lineSeparator())
            }
        }
    return builder.toString()
}

private fun collectionClass(node: Node): String {
    if (node.childrenType() is TextNode) {
        return "String"
    }
    if (node.childrenType() is NumericNode) {
        return "Long"
    }
    if (node.childrenType() == null) {
        return "String"
    }
    return className(node.prefix) + node.name.capitalize()
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

public fun putIntoContext(
    context: Context,
    rootNode: Node,
    entry: Map.Entry<String, JsonNode>
) {
    if (shouldSkip(rootNode)) {
        return
    }
    if (isMapNode(entry.value)) {
        entry.value.fields().forEach {
            val node = context.addNode(Node(rootNode.prefix + "#" + rootNode.name, "", it.value, true))
            if (it.value.isContainerNode) {
                if (it.value.isObject) {
                    putIntoContext(context, node, it)
                } else if (it.value.isArray) {
                    it.value.iterator().forEach { child ->
                        putIntoContext(context, node, object : Map.Entry<String, JsonNode> {
                            override val key: String
                                get() = node.name
                            override val value: JsonNode
                                get() = child
                        })
                    }
                }
            }
        }
    } else {
        entry.value.fields().forEach {
            val node = context.addNode(Node(rootNode.prefix + "#" + rootNode.name, it.key, it.value, isMapNode(it.value)))
            if (it.value.isContainerNode) {
                if (it.value.isObject) {
                    putIntoContext(context, node, it)
                } else if (it.value.isArray) {
                    it.value.iterator().forEach { child ->
                        putIntoContext(context, node, object : Map.Entry<String, JsonNode> {
                            override val key: String
                                get() = node.name
                            override val value: JsonNode
                                get() = child
                        })
                    }
                }
            }
        }
    }

}

fun shouldSkip(rootNode: Node): Boolean {
    val skip = setOf("mail", "error", "interface")
    if (rootNode.prefix == "TestBackendLocale" && skip.contains(rootNode.name)) {
        return true
    }
    return false
}

fun isMapNode(node: JsonNode): Boolean {
    if (node.fields().asSequence().count() > 0) {
        if (node.fields().asSequence().all { q -> isMapIndex(q.key) }) {
            return true
        }
    }
    return false
}

fun isMapIndex(fieldName: String): Boolean {
    if (fieldName.length != "5b47574386f77428ca22b336".length) {
        return false
    }
    if (! fieldName.matches(Regex("[a-f0-9]{24}"))) {
        return false
    }
    return true
}

class Context {
    private val nodes: MutableSet<Node> = mutableSetOf()

    fun addNode(node: Node): Node {
        if (shouldSkip(node)) {
            return node
        }
        nodes.add(node)
        nodes.filter { it.prefix == node.prefix && it.name == node.name }
            .forEach {
                it.updateType(node)
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
    var type: JsonNode,
    val mapNode: Boolean = false
) {

    fun updateType(node: Node) {
        if (type.isArray) {
            val current = childrenType()
            val new = node.childrenType()
            if (current?.javaClass != new?.javaClass) {
                if (current?.javaClass == null) {
                    type = node.type
                } else if (new?.javaClass != null) {
                    throw RuntimeException("Unknown upgrade path for children from ${current.javaClass} to ${new.javaClass}")
                }
            }
        }
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
            if (type.javaClass == TextNode::class.java && node.type.javaClass == NullNode::class.java) {
                return
            }
            if (type.javaClass == ArrayNode::class.java && node.type.javaClass == ObjectNode::class.java) {
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
            JsonNodeType.ARRAY -> "Collection"
            JsonNodeType.BINARY -> "binary"
            JsonNodeType.BOOLEAN -> "Boolean"
            JsonNodeType.MISSING -> "missing"
            JsonNodeType.NULL -> "null"

            JsonNodeType.NUMBER -> {
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

            JsonNodeType.OBJECT -> "Object"
            JsonNodeType.POJO -> "pojo"
            JsonNodeType.STRING -> "String"
        }
    }

    fun childrenType(): JsonNode? {
        if (type.isContainerNode) {
            val arrayNode = type as ContainerNode<*>?
            return arrayNode?.elements()?.asSequence()?.firstOrNull()
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        if (name.toLowerCase() != other.name.toLowerCase()) return false
        if (prefix.toLowerCase() != other.prefix.toLowerCase()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = prefix.toLowerCase().hashCode()
        result = 31 * result + name.toLowerCase().hashCode()
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

private fun cleanGenerated(directory: File) {
    if (directory.exists()) {
        Files.walk(directory.toPath())
            .sorted { o1, o2 -> o2.compareTo(o1) }
            .map(Path::toFile)
            .filter { it.exists() }
            .forEach { it.delete() }
    }

    Files.createDirectories(directory.toPath())
    Files.deleteIfExists(Paths.get(directory.absolutePath, "kode.kt"))
}
