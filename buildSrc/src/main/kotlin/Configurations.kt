import java.math.BigDecimal

object Configurations {

    object Compile {
        val compilerArgs = listOf("-Xjsr305=strict")
    }

    object Tests {
        val testSets = listOf("integTest", "apiTest")
        val minimumCoverage = BigDecimal("0.90")
    }
}
