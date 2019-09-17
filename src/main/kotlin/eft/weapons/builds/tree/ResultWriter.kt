package eft.weapons.builds.tree

import eft.weapons.builds.items.templates.TestItemTemplatesData
import eft.weapons.builds.utils.Locale.itemShortName
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

class ResultWriter(private val csvPrinter: CSVPrinter) {

    fun writeLine(items: Collection<String>) {
        csvPrinter.printRecord(items.filter { it != "EMPTY" })
    }

    fun writeLineS(items: Collection<Slot>) {
        csvPrinter.printRecord(items.flatMap { it.items }.filter { it != "EMPTY" })
    }

    fun close() {
        csvPrinter.close(true)
    }

}

fun draftPrinter(weapon: TestItemTemplatesData): ResultWriter {
    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csvDrafts"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${itemShortName(weapon.id)}.csv")
    val fileWriter = FileWriter(csvFile.toFile())
    return ResultWriter(CSVPrinter(fileWriter, CSVFormat.DEFAULT))
}

fun draftReader(weapon: TestItemTemplatesData): Stream<String> {
    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csvDrafts"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${itemShortName(weapon.id)}.csv")
    return Files.lines(csvFile)
}

fun validDraftsReader(weapon: TestItemTemplatesData): Stream<String> {
    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csvValidBuilds"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${itemShortName(weapon.id)}.csv")
    return Files.lines(csvFile)
}

fun buildsPrinter(weapon: TestItemTemplatesData): ResultWriter {
    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csvValidBuilds"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${itemShortName(weapon.id)}.csv")
    val fileWriter = FileWriter(csvFile.toFile())
    return ResultWriter(CSVPrinter(fileWriter, CSVFormat.DEFAULT))
}

fun finalBuildsPrinter(weapon: TestItemTemplatesData): ResultWriter {
    val csvDir = Paths.get(
        Paths.get(System.getProperty("user.dir")).toString(),
        "build",
        "csvFinal"
    )
    csvDir.toFile().mkdirs()
    val csvFile = Paths.get(csvDir.toString(), "${itemShortName(weapon.id)}.csv")
    val fileWriter = FileWriter(csvFile.toFile())
    return ResultWriter(CSVPrinter(fileWriter, CSVFormat.DEFAULT))
}