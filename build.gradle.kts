import eft.weapons.builds.parseBytes
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.41"
    application
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.9.9"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jooq:jooq:3.12.1")
    implementation("org.xerial:sqlite-jdbc:3.28.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.apache.commons:commons-text:1.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-testng")
    testImplementation("org.hamcrest:hamcrest:2.1")
}

application {
    mainClassName = "eft.weapons.builds.AppKt"
}

val generatedSources = File(buildDir, "generated")

kotlin {
    sourceSets {
        get("main").kotlin.srcDir(generatedSources)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks {
    val copyResources by creating(Copy::class) {
        from("$rootDir/TextAsset")
        into("$buildDir/resources/main")
        rename {
            it.replace(".bytes", ".json")
        }
    }

    val parseItemsFile by creating() {
        doLast {
            parseBytes(generatedSources, project)
        }
    }
}

tasks["compileKotlin"].dependsOn("parseItemsFile")
tasks["processResources"].dependsOn("copyResources")

