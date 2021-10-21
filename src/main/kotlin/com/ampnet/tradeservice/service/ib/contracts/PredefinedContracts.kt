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

    fun contracts() = listOf(tesla, apple, amazon, facebook, microsoft, nvidia, disney).map { it.toContract() }

    fun Pair<String, Int>.toContract(): Contract {
        val symbol = this.first
        val id = this.second
        return Contract().apply {
            conid(id)
            symbol(symbol)
            secType("STK")
            currency("USD")
            exchange("ISLAND")
        }
    }
}
