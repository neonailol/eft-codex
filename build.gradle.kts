import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("kapt") version "1.3.50"
    id("com.github.ben-manes.versions") version "0.25.0"
    application
}

repositories {
    mavenLocal()
    jcenter()
    maven("https://jitpack.io")
}

dependencies {

    implementation(project(":json-model-generator"))
    kapt(project(":json-model-generator"))

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.10.0"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("org.jooq:jooq:3.12.1")
    implementation("org.xerial:sqlite-jdbc:3.28.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-csv:1.7")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.apache.commons:commons-text:1.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-testng")
    testImplementation("org.hamcrest:hamcrest:2.2-rc1")
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

    withType(Test::class.java) {
        useTestNG()
        testLogging {
            showStandardStreams = true
            events("passed", "failed")
        }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        version = "5.6.2"
    }
}

