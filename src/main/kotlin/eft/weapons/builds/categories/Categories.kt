package eft.weapons.builds.categories

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.utils.Items

@JsonPropertyOrder(value = ["id", "name", "children"])
class ItemCategories(
    @JsonIgnore
    val root: TestItemTemplatesData,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val children: List<ItemCategories> = listOf()
) {

    var id: String = root.id
    var name: String = root.name
}

fun children(
    root: TestItemTemplatesData,
    parents: List<TestItemTemplatesData>
): List<ItemCategories> {
    val children = Items.children(root.id)
    if (children.isEmpty()) {
        return emptyList()
    }
    return children.filter { parents.any { p -> p.id == it.id } }.map {
        ItemCategories(
            it,
            children(
                it,
                parents
            )
        )
    }
}