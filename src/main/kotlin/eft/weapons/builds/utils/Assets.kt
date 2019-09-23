package eft.weapons.builds.utils

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream

fun openAsset(name: String): InputStream {
    return Items::class.java.getResourceAsStream("/$name")
}

inline fun <reified T : Any> loadBytes(name: String): T {
    val mapper = mapper()
    val json = openAsset(name)
    return mapper.readValue(json)
}
