package eft.weapons.builds.utils

import eft.weapons.builds.items.templates.TestItemTemplatesData

fun isMatters(itemId: String): Boolean {
    val item = Items[itemId]
    if (haveParentNamed(item, listOf("Sights", "TacticalCombo", "Flashlight", "Magazine", "Ammo", "Launcher", "Charge"))) {
        return false
    }
    if (isScopeOrTacticalOnlyMount(item)) {
        return false
    }
    return true
}

fun haveParentNamed(item: TestItemTemplatesData, excluded: String): Boolean {
    return haveParentNamed(item, listOf(excluded))
}

fun haveParentNamed(item: TestItemTemplatesData, excluded: Collection<String>): Boolean {
    var parent = item.parent
    while (parent != "") {
        val parentItem = Items[parent]
        if (excluded.contains(parentItem.name)) {
            return true
        }
        parent = parentItem.parent
    }
    return false
}

fun isScopeOrTacticalOnlyMount(item: TestItemTemplatesData): Boolean {
    if (item.props.slots.isEmpty()) {
        return false
    }
    return item.props.slots
        .flatMap { it.props.filters }
        .flatMap { it.filter }
        .map { Items[it] }
        .all {
            haveParentNamed(it, listOf("TacticalCombo", "Flashlight", "Sights")) || isScopeOrTacticalOnlyMount(it)
        }
}

