package eft.weapons.builds

import com.google.auto.service.AutoService
import org.yanex.takenoko.PrettyPrinter
import org.yanex.takenoko.PrettyPrinterConfiguration
import org.yanex.takenoko.getterExpression
import org.yanex.takenoko.kotlinFile
import org.yanex.takenoko.receiverType
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Target(AnnotationTarget.CLASS)
annotation class ImportAssetDefinition

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("eft.weapons.builds.ImportAssetDefinition")
@SupportedOptions(AnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val annotatedElements = roundEnv.getElementsAnnotatedWith(ImportAssetDefinition::class.java)
        if (annotatedElements.isEmpty()) return false

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        val generatedKtFile = kotlinFile("test.generated") {
            for (element in annotatedElements) {
                val typeElement = element.toTypeElementOrNull() ?: continue

                property("simpleClassName") {
                    receiverType(typeElement.qualifiedName.toString())
                    getterExpression("this::class.java.simpleName")
                }
            }
        }

        File(kaptKotlinGeneratedDir, "testGenerated.kt").apply {
            parentFile.mkdirs()
            writeText(generatedKtFile.accept(PrettyPrinter(PrettyPrinterConfiguration())))
        }

        return true
    }

    fun Element.toTypeElementOrNull(): TypeElement? {
        if (this !is TypeElement) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Invalid element type, class expected", this)
            return null
        }

        return this
    }
}