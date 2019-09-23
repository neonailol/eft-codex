package eft.weapons.builds

import java.nio.file.Files
import kotlin.test.Test

class ModelsCodeGenTest {

    @Test
    fun test() {
        val tempDir = createTempDir()
        parseTraders(tempDir)
        println(tempDir)
        Files.walk(tempDir.toPath()).forEach {
            if (it.toFile().isFile) {
                print(Files.readString(it))
            }
        }
    }
}