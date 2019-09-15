package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestBackendLocale

object Locale {

    private var locale: TestBackendLocale = loadBytes("locale.json")
    private val alternate: MutableMap<String, String> = HashMap()

    init {
        alternate["5b3baf8f5acfc40dc5296692"] = "116mm 7.62x25 TT barrel Gold"
    }

    fun itemName(id: String): String {
        val itemName = alternate.getOrDefault(id, locale.data.templates[id]?.name) ?: id
        return itemName.replace('\n', ' ')
    }

    fun itemShortName(id: String): String {
        val itemName = alternate.getOrDefault(id, locale.data.templates[id]?.shortName) ?: id
        return itemName.replace('\n', ' ')
    }
}