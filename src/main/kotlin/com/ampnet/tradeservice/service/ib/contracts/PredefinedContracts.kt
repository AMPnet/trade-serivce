package com.ampnet.tradeservice.service.ib.contracts

import com.ib.client.Contract

@Suppress("MagicNumber")
object PredefinedContracts {

    val tesla = "TSLA" to 76792991
    val apple = "AAPL" to 265598
    val amazon = "AMZN" to 3691937
    val facebook = "FB" to 107113386
    val microsoft = "MSFT" to 272093
    val nvidia = "NVDA" to 4815747
    val disney = "DIS" to 6459

    val historicalData = mapOf(
        76792991 to 950.67, // tesla
        265598 to 148.66, // apple
        3691937 to 3350.0, // amazon
        107113386 to 320.2, // facebook
        272093 to 309.2, // microsoft
        4815747 to 229.64, // nvidia
        6459 to 169.97 // disney
    )

    fun contracts() = listOf(tesla, apple, amazon, facebook, microsoft, nvidia, disney).map { it.toContract() }

    fun Pair<String, Int>.toContract(): Contract {
        val symbol = this.first
        return this.second.toContract().apply {
            symbol(symbol)
        }
    }

    fun Int.toContract(): Contract {
        val id = this
        return Contract().apply {
            conid(id)
            secType("STK")
            currency("USD")
            exchange("ISLAND")
        }
    }
}
