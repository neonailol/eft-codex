package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import eft.weapons.builds.items.templates.TestBackendLocale
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

fun mapper(): ObjectMapper {
    return ObjectMapper().findAndRegisterModules().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
}

fun stringBuilder(any: Any): String {
    return mapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(any)
}

fun openAsses(name: String): InputStream {
    val path = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "TextAsset",
        name
    )
    return Files.newInputStream(path)
}

fun itemName(id: String): String {
    val mapper = mapper()
    val json = openAsses("TestBackendLocaleEn.bytes")
    val locale = mapper.readValue(json, TestBackendLocale::class.java)
    return locale.data.templates[id]?.name ?: id
}

fun main(args: Array<String>) {

    // Try PM Pistol, TT Pistol, TOZ shotgun
    println(args)
}
