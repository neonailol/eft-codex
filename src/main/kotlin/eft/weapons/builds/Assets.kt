package eft.weapons.builds

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

fun openAsset(name: String): InputStream {
    val path = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "assets",
        name
    )
    return Files.newInputStream(path)
}

inline fun <reified T : Any> loadBytes(name: String): T {
    val mapper = mapper()
    val json = openAsset(name)
    return mapper.readValue(json)
}
