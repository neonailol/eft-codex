package eft.weapons.builds.tree

import eft.weapons.builds.utils.Items
import eft.weapons.builds.utils.stringBuilder
import org.testng.annotations.Test
import java.time.LocalDateTime
import kotlin.streams.asSequence

class TreeAlgorithmTest {

    @Test
    fun `can list all tt builds`() {
        val weapon = Items["571a12c42459771f627b58a0"]
        val resultWriter = draftPrinter(weapon)
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
        val transform = transform(tree)
        println(stringBuilder(transform))
        val values = transform.values.toList().distinct()
        println(stringBuilder(permutations(resultWriter, values)))
        resultWriter.close()
        println(draftReader(weapon).count())
    }

    @Test
    fun `can list all as val builds`() {
        val weapon = Items["57c44b372459772d2b39b8ce"]
        val resultWriter = draftPrinter(weapon)
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
        val transform = transform(tree)
        println(stringBuilder(transform))
        val values = transform.values.toList().distinct()
        println(stringBuilder(permutations(resultWriter, values)))
        resultWriter.close()
        println(draftReader(weapon).count())
    }

    @Test
    fun `can print all aks-74un builds`() {
        val weapon = Items["583990e32459771419544dd2"]
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
        val transform = transform(tree)
        println(stringBuilder(transform))
    }

    @Test
    fun `can list all aks-74un builds`() {
        val weapon = Items["583990e32459771419544dd2"]
        val resultWriter = draftPrinter(weapon)
        val tree = itemTree(weapon)
        println(stringBuilder(tree))
        val transform = transform(tree)
        println(stringBuilder(transform))
        val values = transform.values.toList().distinct()
        println(stringBuilder(permutations(resultWriter, values)))
        resultWriter.close()
        println(draftReader(weapon).count())
    }

    //59f8a37386f7747af3328f06
//5c791e872e2216001219c40a
    @Test
    fun `can list all aks-74un builds from draft`() {
        val weapon = Items["583990e32459771419544dd2"]
        val buildsPrinter = buildsPrinter(weapon)
        val total = draftReader(weapon).count()
        println("total = $total")
        var progress = 0
        draftReader(weapon).asSequence().withIndex().forEach {
            var newProgress = ((it.index.toDouble() / total.toDouble()) * 100).toInt()
            if (newProgress > progress) {
                progress = newProgress
                println("${LocalDateTime.now()} - $progress")
            }
            val slots = it.value.split(',')
            if (isValidBuild(weapon, slots)) {
                buildsPrinter.writeLine(slots)
            }
        }
        buildsPrinter.close()
    }

    @Test
    fun `can pretty print all aks-74un builds from draft`() {
        val weapon = Items["583990e32459771419544dd2"]
        prettyPrintBuilds(weapon)
    }

}