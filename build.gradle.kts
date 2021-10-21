import io.gitlab.arturbosch.detekt.Detekt
import nu.studer.gradle.jooq.JooqGenerate
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    kotlin("jvm").version(Versions.Compile.kotlin)
    kotlin("plugin.spring").version(Versions.Compile.kotlin)

    id("org.jlleitschuh.gradle.ktlint").version(Versions.Plugins.ktlint)
    id("io.gitlab.arturbosch.detekt").version(Versions.Plugins.detekt)
    id("org.unbroken-dome.test-sets").version(Versions.Plugins.testSets)
    id("com.adarshr.test-logger").version(Versions.Plugins.testLogger)
    id("org.springframework.boot").version(Versions.Plugins.springBoot)
    id("io.spring.dependency-management").version(Versions.Plugins.springDependencyManagement)
    id("org.web3j").version(Versions.Plugins.web3j)
    id("org.flywaydb.flyway").version(Versions.Plugins.flyway)
    id("nu.studer.jooq").version(Versions.Plugins.jooq)
    id("application")

    idea
    jacoco
}

extensions.configure(KtlintExtension::class.java) {
    version.set(Versions.Tools.ktlint)
}

group = "com.ampnet"
version = Versions.project
java.sourceCompatibility = Versions.Compile.sourceCompatibility
java.targetCompatibility = Versions.Compile.targetCompatibility

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}

testSets {
    Configurations.Tests.testSets.forEach { create(it) }
}

node {
    nodeProjectDir.set(file("node/"))
}

solidity {
    version = Versions.Tools.solidity
}

web3j {
    generatedPackageName = "com.ampnet.tradeservice.generated.contract"
}

sourceSets.main {
    java.srcDirs(
        "$buildDir/generated/sources/web3j/main/java"
    )
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDir("$buildDir/generated/sources/jooq/main/kotlin")
    }
}

val flywayMigration by configurations.creating

fun DependencyHandler.integTestImplementation(dependencyNotation: Any): Dependency? =
    add("integTestImplementation", dependencyNotation)

fun DependencyHandler.kaptIntegTest(dependencyNotation: Any): Dependency? =
    add("kaptIntegTest", dependencyNotation)

fun DependencyHandler.apiTestImplementation(dependencyNotation: Any): Dependency? =
    add("apiTestImplementation", dependencyNotation)

fun DependencyHandler.kaptApiTest(dependencyNotation: Any): Dependency? =
    add("kaptApiTest", dependencyNotation)

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:postgresql")
    flywayMigration(Configurations.Database.driverDependency)
    jooqGenerator(Configurations.Database.driverDependency)

    implementation("org.web3j:core:${Versions.Dependencies.web3j}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Dependencies.kotlinCoroutines}")
    implementation("org.jobrunr:jobrunr-spring-boot-starter:${Versions.Dependencies.jobrunr}")
    implementation("io.github.microutils:kotlin-logging-jvm:${Versions.Dependencies.kotlinLogging}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${Versions.Dependencies.mockitoKotlin}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${Versions.Dependencies.assertk}")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    integTestImplementation("org.testcontainers:testcontainers:${Versions.Dependencies.testContainers}")
    integTestImplementation("com.github.tomakehurst:wiremock:${Versions.Dependencies.wireMock}")
    integTestImplementation(sourceSets.test.get().output)

    apiTestImplementation("com.github.tomakehurst:wiremock:${Versions.Dependencies.wireMock}")
    apiTestImplementation(sourceSets.test.get().output)
}

flyway {
    url = Configurations.Database.url
    user = Configurations.Database.user
    password = Configurations.Database.password
    schemas = arrayOf(Configurations.Database.schema)
    configurations = arrayOf("flywayMigration")
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = Configurations.Database.driverClass
                    url = Configurations.Database.url
                    user = Configurations.Database.user
                    password = Configurations.Database.password
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        inputSchema = Configurations.Database.schema
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isImmutableInterfaces = true
                        isFluentSetters = false
                    }
                    target.apply {
                        packageName = "com.ampnet.tradeservice.generated.jooq"
                        directory = "$buildDir/generated/sources/jooq/main/kotlin"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks.withType<JooqGenerate> {
    dependsOn(tasks["flywayMigrate"])
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = Configurations.Compile.compilerArgs
        jvmTarget = Versions.Compile.jvmTarget
    }
    dependsOn(tasks["generateJooq"])
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task("fullTest") {
    val allTests = listOf(tasks.test) + Configurations.Tests.testSets.map { tasks[it] }
    dependsOn(*allTests.toTypedArray())
}

jacoco.toolVersion = Versions.Tools.jacoco
tasks.withType<JacocoReport> {
    val allTestExecFiles = (listOf("test") + Configurations.Tests.testSets)
        .map { "$buildDir/jacoco/$it.exec" }
    executionData(*allTestExecFiles.toTypedArray())

    reports {
        xml.isEnabled = true
        xml.destination = file("$buildDir/reports/jacoco/report.xml")
        csv.isEnabled = false
        html.destination = file("$buildDir/reports/jacoco/html")
    }
    sourceDirectories.setFrom(listOf(file("${project.projectDir}/src/main/kotlin")))
    classDirectories.setFrom(
        fileTree("$buildDir/classes/kotlin/main").apply {
            exclude("com/ampnet/tradeservice/generated/**")
        }
    )
    dependsOn(tasks["fullTest"])
}

tasks.withType<JacocoCoverageVerification> {
    val allTestExecFiles = (listOf("test") + Configurations.Tests.testSets)
        .map { "$buildDir/jacoco/$it.exec" }
    executionData(*allTestExecFiles.toTypedArray())

    sourceDirectories.setFrom(listOf(file("${project.projectDir}/src/main/kotlin")))
    classDirectories.setFrom(
        fileTree("$buildDir/classes/kotlin/main").apply {
            exclude("com/ampnet/tradeservice/generated/**")
        }
    )

    violationRules {
        rule {
            limit {
                minimum = Configurations.Tests.minimumCoverage
            }
        }
    }
    mustRunAfter(tasks.jacocoTestReport)
}

detekt {
    input = files("src/main/kotlin")
    config = files("detekt-config.yml")
}

tasks.withType<Detekt> {
    exclude("com/ampnet/tradeservice/generated/**")
}

ktlint {
    filter {
        exclude("com/ampnet/tradeservice/generated/**")
    }
}

task("qualityCheck") {
    dependsOn(tasks.ktlintCheck, tasks.detekt, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}
