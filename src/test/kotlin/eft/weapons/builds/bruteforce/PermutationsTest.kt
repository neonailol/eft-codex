package eft.weapons.builds.bruteforce

import eft.weapons.builds.utils.Items
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.testng.annotations.Test

class PermutationsTest {

    @Test
    fun `can list all tt builds`() {
        val weapon = Items["571a12c42459771f627b58a0"]
        val builds = weaponBuilds(weapon)
        assertThat(printBuilds(weapon, builds), equalTo(37))
    }

    @Test(enabled = false)
    fun `can list all as val builds`() {
        val weapon = Items["57c44b372459772d2b39b8ce"]
        val builds = weaponBuilds(weapon)
        assertThat(printBuilds(weapon, builds), equalTo(6350))
    }

    @Test(enabled = false)
    fun `can list all aks-74un builds`() {
        val weapon = Items["583990e32459771419544dd2"]
        val builds = weaponBuilds(weapon)
        assertThat(printBuilds(weapon, builds), equalTo(- 1))
    }

}