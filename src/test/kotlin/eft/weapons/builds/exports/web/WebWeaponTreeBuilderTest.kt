package eft.weapons.builds.exports.web

import eft.weapons.builds.tree.weaponBuilds
import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.printJson
import eft.weapons.builds.utils.stringBuilder
import org.testng.annotations.Test

class WebWeaponTreeBuilderTest {

    @Test
    fun `can export ak-74n`() {
        val weapon = Items["5644bd2b4bdc2d3b4c8b4572"]
        val itemTree = weaponBuilds(weapon)
        val weaponTree = processTree(itemTree)
        printJson(weaponTree)
        val tempFile = createTempFile(suffix = ".json")
        tempFile.writeText(stringBuilder(weaponTree))
        println(tempFile.absolutePath)
    }
}