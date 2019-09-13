package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import eft.weapons.builds.items.templates.TestBackendLocale
import eft.weapons.builds.items.templates.TestItemTemplates
import eft.weapons.builds.items.templates.TestItemTemplatesData
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

fun openAsset(name: String): InputStream {
    val path = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "TextAsset",
        name
    )
    return Files.newInputStream(path)
}

fun <T : Any> loadBytes(name: String, clazz: Class<T>): T {
    val mapper = mapper()
    val json = openAsset(name)
    return mapper.readValue(json, clazz)
}

object Locale {

    var locale: TestBackendLocale

    init {
        val mapper = mapper()
        val json = openAsset("TestBackendLocaleEn.bytes")
        locale = mapper.readValue(json, TestBackendLocale::class.java)
    }

    fun itemName(id: String): String {
        return locale.data.templates[id]?.name ?: id
    }
}

fun TestItemTemplates.getItem(id: String): TestItemTemplatesData {
    return this.data[id] ?: throw IllegalStateException("Unknown id: $id")
}

fun main(args: Array<String>) {

    // Try PM Pistol, TT Pistol, TOZ shotgun
    println(args)
}
