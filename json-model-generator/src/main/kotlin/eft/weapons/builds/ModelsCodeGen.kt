package eft.weapons.builds

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.HashSetValuedHashMap
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.apache.commons.text.WordUtils
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

fun mapper(): ObjectMapper {
    return ObjectMapper().findAndRegisterModules()
}

fun openAsset(name: String): InputStream {
    return Context::class.java.getResourceAsStream("/$name")
}

fun parseBytes(directory: File) {
    paresItemTemplates(directory)
    parseLocale(directory)
    parseTraders(directory)
}

fun parseTraders(directory: File) {
    val ignores: MultiValuedMap<String, String> = HashSetValuedHashMap()
    processAssets(
        directory,
        listOf(
            "traders/mechanic.json",
            "traders/peacekeeper.json",
            "traders/prapor.json",
            "traders/ragman.json",
            "traders/skier.json",
            "traders/therapist.json"
        ),
        "TestTraders",
        ignores
    )
}

fun parseLocale(directory: File) {
    val ignores: MultiValuedMap<String, String> = HashSetValuedHashMap()
    ignores.put("TestBackendLocaleData", "mail")
    ignores.put("TestBackendLocaleData", "error")
    ignores.put("TestBackendLocaleData", "interface")
    ignores.put("TestBackendLocaleDataTemplates", "#_val")
    processAsset(directory, "locale.json", "TestBackendLocale", ignores)
}

private fun paresItemTemplates(directory: File) {
    val ignores: MultiValuedMap<String, String> = HashSetValuedHashMap()
    ignores.put("TestItemTemplatesDataProps", "Buffs")
    processAsset(directory, "items.json", "TestItemTemplates", ignores)
}

fun processAsset(directory: File, asset: String, name: String, ignores: MultiValuedMap<String, String>) {
    val mapper = mapper()
    val json = openAsset(asset)
    val tree = mapper.readTree(json)
    val context = Context(ignores)
    tree.fields().forEach {
        val rootNode = context.addNode(Node(name, it.key, it.value, isMapNode(it.value), false))
        if (it.value.isContainerNode) {
            putIntoContext(context, rootNode, it)
        }
    }
    val codeGeneration = codeGeneration(context)
    val createFile = Files.createFile(Paths.get(directory.absolutePath, "$name.kt"))
    createFile.toFile().writeText(codeGeneration)
}

fun processAssets(directory: File, assets: Collection<String>, name: String, ignores: MultiValuedMap<String, String>) {
    val mapper = mapper()
    val context = Context(ignores)
    for (asset in assets) {
        val json = openAsset(asset)
        val tree = mapper.readTree(json)
        tree.fields().forEach {
            val rootNode = context.addNode(Node(name, it.key, it.value, isMapNode(it.value), false))
            if (it.value.isContainerNode) {
                putIntoContext(context, rootNode, it)
            }
        }
    }
    val codeGeneration = codeGeneration(context)
    val createFile = Files.createFile(Paths.get(directory.absolutePath, "$name.kt"))
    createFile.toFile().writeText(codeGeneration)
}

fun codeGeneration(context: Context): String {
    val builder = StringBuilder()
    builder.append("package eft.weapons.builds.items.templates" + System.lineSeparator())
    builder.append("import com.fasterxml.jackson.annotation.JsonProperty" + System.lineSeparator())
    builder.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties" + System.lineSeparator())
    builder.append("import eft.weapons.builds.utils.stringBuilder" + System.lineSeparator())
    builder.append(System.lineSeparator())
    context.nodes()
        .asSequence()
        .groupBy { it.prefix }
        .toSortedMap()
        .forEach { (clazz, props) ->
            val clazzName = className(clazz)

            context.ignoredFields.keys()
                .firstOrNull { it == clazzName }
                .let {
                    val fields = context.ignoredFields.get(it).joinToString(",", transform = { "\"$it\"" })
                    builder.append("@JsonIgnoreProperties($fields)" + System.lineSeparator())
                }

            if (props.size == 1 && props[0].mapNode) {
                val type = when {
                    props[0].type is TextNode -> "String"
                    props[0].type is IntNode -> "Int"
                    else -> clazzName
                }
                if (props[0].array) {
                    builder.append("class $clazzName : ArrayList<${type}Array>() {" + System.lineSeparator())
                    builder.append("    override fun toString(): String = stringBuilder(this)" + System.lineSeparator())
                    builder.append("}" + System.lineSeparator() + System.lineSeparator())

                    builder.append("class ${clazzName}Array : ArrayList<${type}ArrayInt>() {" + System.lineSeparator())
                    builder.append("    override fun toString(): String = stringBuilder(this)" + System.lineSeparator())
                    builder.append("}" + System.lineSeparator() + System.lineSeparator())
                } else {
                    if (props[0].type is ArrayNode) {
                        builder.append("class ${clazzName}Map : HashMap<String, ${type}Data>() {" + System.lineSeparator())
                    } else {
                        builder.append("class ${clazzName}Map : HashMap<String, ${type}>() {" + System.lineSeparator())
                    }

                    builder.append("    override fun toString(): String = stringBuilder(this)" + System.lineSeparator())
                    builder.append("}" + System.lineSeparator() + System.lineSeparator())
                }

            } else {
                builder.append("class $clazzName {" + System.lineSeparator())
                props.forEachIndexed { index, node ->

                    val nodeType = when (node.typeString()) {
                        "Object" -> className(node.prefix + "_" + node.name)
                        "Collection" -> "Collection<${collectionClass(node)}>"
                        else -> node.typeString()
                    }

                    val mapNode = when {
                        node.mapNode -> "Map"
                        else -> ""
                    }
                    if (node.name != "Buffs" && node.name != "#_val") {
                        val postfix = when (index) {
                            props.size - 1 -> ""
                            else -> "" + System.lineSeparator()
                        }
                        builder.append("    @JsonProperty(\"${node.name}\")" + System.lineSeparator())
                        val nullable = if (node.haveNullValues) {
                            "?"
                        } else {
                            ""
                        }
                        val init = if (nodeType == "Int" || nodeType == "Long") {
                            " = 0"
                        } else if (nodeType == "Double") {
                            " = 0.0"
                        } else if (nodeType == "Boolean") {
                            " = false"
                        } else if (node.typeString() == "Collection") {
                            " = listOf()"
                        } else {
                            " = ${nodeType}${mapNode}()"
                        }

                        val cleanName = node.name
                            .removePrefix("is")
                            .removePrefix("_")
                            .split("_")
                            .map { it.capitalize() }
                            .joinToString("")
                            .decapitalize()
                        builder.append("    var ${cleanName}: ${nodeType}${mapNode}${nullable}${init}" + postfix)
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

fun putIntoContext(
    context: Context,
    rootNode: Node,
    entry: Map.Entry<String, JsonNode>
) {
    if (context.shouldSkip(rootNode)) {
        return
    }

    val isEntryMapNode = isMapNode(entry.value)

    entry.value.fields().forEach {
        val nodeName: String = when (isEntryMapNode) {
            true -> ""
            false -> it.key
        }
        val isMapNode: Boolean = when (isEntryMapNode) {
            true -> true
            false -> isMapNode(it.value)
        }
        val node = context.addNode(Node(rootNode.prefix + "#" + rootNode.name, nodeName, it.value, isMapNode, false))
        if (it.value.isContainerNode) {
            if (it.value.isObject) {
                putIntoContext(context, node, it)
            } else if (it.value.isArray) {
                it.value.iterator().forEach { child ->
                    putIntoContext(context, node, MapEntry(node.name, child))
                }
            }
        }
    }
    if (entry.value.isArray) {
        val node = context.addNode(Node(rootNode.prefix + "#" + "Data", "ArrayInt", entry.value, true, true))
        val node1 = context.addNode(Node(node.prefix, "ArrayInt", entry.value, true, true))
        entry.value.forEach {
            putIntoContext(context, node, MapEntry(node1.name, it))
        }
    }
}

data class MapEntry<out K, out V>(override val key: K, override val value: V) : Map.Entry<K, V>

fun isMapNode(node: JsonNode): Boolean {
    return when {
        node.fields().asSequence().count() == 0 -> false
        else -> node.fields().asSequence().all { isMapIndex(it.key) }
    }
}

fun isMapIndex(fieldName: String): Boolean {
    return when {
        fieldName.length != "5b47574386f77428ca22b336".length -> false
        fieldName.matches(Regex("[a-f0-9]{24}")) -> true
        else -> false
    }
}

class Context(val ignoredFields: MultiValuedMap<String, String>) {

    private val nodes: MutableSet<Node> = mutableSetOf()

    fun addNode(node: Node): Node {
        if (shouldSkip(node)) {
            return node
        }
        nodes.add(node)
        nodes.asSequence()
            .filter { it.prefix == node.prefix && it.name == node.name }
            .forEach { it.updateType(node) }
        return node
    }

    fun nodes(): Set<Node> {
        return HashSet(nodes)
    }

    fun shouldSkip(rootNode: Node): Boolean {
        val className = className(rootNode.prefix)
        return ignoredFields.keys()
            .asSequence()
            .filter { it == className }
            .flatMap { ignoredFields.get(it).asSequence() }
            .any { it == rootNode.name }
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
    val mapNode: Boolean = false,
    val array: Boolean
) {

    var haveNullValues = type.isNull

    fun updateType(node: Node) {
        if (! haveNullValues && node.type.isNull) {
            haveNullValues = true
        }
        //TestBackendLocaleDataTemplates FoldedSlot
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
            if (type.javaClass == NullNode::class.java && node.type.javaClass == TextNode::class.java) {
                type = node.type
                return
            }
            if (type.javaClass == IntNode::class.java && node.type.javaClass == TextNode::class.java) {
                type = node.type
                return
            }
            if (type.javaClass == DoubleNode::class.java && node.type.javaClass == IntNode::class.java) {
                return
            }
            if (type.javaClass == TextNode::class.java && node.type.javaClass == IntNode::class.java) {
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
            throw RuntimeException("Unknown upgrade path from ${type.javaClass} to ${node.type.javaClass}")
        }
    }

    fun typeString(): String {
        return when (type.nodeType) {
            JsonNodeType.ARRAY -> "Collection"
            JsonNodeType.BINARY -> "Binary"
            JsonNodeType.BOOLEAN -> "Boolean"
            JsonNodeType.MISSING -> "Missing"
            JsonNodeType.NULL -> "Any"

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
            JsonNodeType.POJO -> "POJO"
            JsonNodeType.STRING -> "String"
            null -> throw RuntimeException("Node type is null")
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
