import java.math.BigDecimal

object Configurations {

    object Compile {
        val compilerArgs = listOf("-Xjsr305=strict")
    }

    object Database {
        val url = "jdbc:postgresql://localhost:5432/postgres"
        val user = "postgres"
        val password = "postgres"
        val schema = "trade_service"
        val driverDependency = "org.postgresql:postgresql:${Versions.Dependencies.postgresDriver}"
        val driverClass = "org.postgresql.Driver"
    }

    object Tests {
        val testSets = listOf("integTest", "apiTest")
        val minimumCoverage = BigDecimal("0.90")
    }
}
