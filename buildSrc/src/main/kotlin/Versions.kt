import org.gradle.api.JavaVersion

object Versions {

    const val project = "0.0.3"

    object Compile {
        const val kotlin = "1.5.31"
        val sourceCompatibility = JavaVersion.VERSION_11
        val targetCompatibility = JavaVersion.VERSION_11
        val jvmTarget = targetCompatibility.name.removePrefix("VERSION_").replace('_', '.')
    }

    object Plugins {
        const val ktlint = "10.0.0"
        const val detekt = "1.17.1"
        const val testSets = "4.0.0"
        const val testLogger = "3.0.0"
        const val springBoot = "2.5.5"
        const val springDependencyManagement = "1.0.11.RELEASE"
        const val web3j = "4.8.4"
        const val flyway = "8.0.1"
        const val jooq = "6.0.1"
        const val jib = "3.1.4"
    }

    object Tools {
        const val ktlint = "0.41.0"
        const val jacoco = "0.8.7"
        const val solidity = "0.8.0"
    }

    object Dependencies {
        const val web3j = "4.8.7"
        const val okHttp = "4.9.1"
        const val kotlinCoroutines = "1.5.2"
        const val kotlinLogging = "2.0.6"
        const val mockitoKotlin = "3.2.0"
        const val assertk = "0.24"
        const val wireMock = "2.27.2"
        const val testContainers = "1.15.3"
        const val postgresDriver = "42.3.0"
        const val jobrunr = "4.0.0"
    }
}
