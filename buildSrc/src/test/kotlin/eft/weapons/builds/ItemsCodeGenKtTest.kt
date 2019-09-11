package eft.weapons.builds

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
    fun `can load locale`() {
        val mapper = mapper()
        val path = Paths.get(
            Paths.get(System.getProperty("user.dir")).parent.toString(),
            "TextAsset",
            "TestBackendLocaleEn.bytes"
        )
        val readTree = mapper.readTree(Files.newInputStream(path))

        val context = Context()
        val types = HashSet<String>()
        for (datum in readTree) {
            datum.fields().forEach {
                val rootNode = context.addNode(Node("TestBackendLocale", it.key, it.value, isMapNode(it.value)))
                if (it.value.isContainerNode) {
                    putIntoContext(context, rootNode, it)
                }
            }
        }
        val codeGeneration = codeGeneration(context)
        println(codeGeneration)
    }
}
