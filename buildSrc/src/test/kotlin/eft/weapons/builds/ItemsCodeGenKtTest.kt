package eft.weapons.builds

import org.apache.commons.collections4.multimap.HashSetValuedHashMap
import org.testng.annotations.Test
import java.nio.file.Files
import java.nio.file.Paths

/**
 * TODO:
 *
 * @author Valerii Zhirnov (vazhirnov@yamoney.ru)
 * @since 11.09.2019
 */
class ItemsCodeGenKtTest {

    @Test
    fun `can load items`() {
        val mapper = mapper()
        val path = Paths.get(
            Paths.get(System.getProperty("user.dir")).parent.toString(),
            "TextAsset",
            "TestItemTemplates.bytes"
        )
        val readTree = mapper.readTree(Files.newInputStream(path))
        val context = Context(HashSetValuedHashMap())
        readTree.fields().forEach {
            val rootNode = context.addNode(Node("TestItemTemplates", it.key, it.value, isMapNode(it.value)))
            if (it.value.isContainerNode) {
                putIntoContext(context, rootNode, it)
            }
        }
        val codeGeneration = codeGeneration(context)
        println(codeGeneration)
    }

    @Test
    fun `can load locale`() {
        val mapper = mapper()
        val path = Paths.get(
            Paths.get(System.getProperty("user.dir")).parent.toString(),
            "TextAsset",
            "TestBackendLocaleEn.bytes"
        )
        val readTree = mapper.readTree(Files.newInputStream(path))
        val context = Context(HashSetValuedHashMap())
        readTree.fields().forEach {
            val rootNode = context.addNode(Node("TestBackendLocale", it.key, it.value, isMapNode(it.value)))
            if (it.value.isContainerNode) {
                putIntoContext(context, rootNode, it)
            }
        }
        val codeGeneration = codeGeneration(context)
        println(codeGeneration)
    }
}
