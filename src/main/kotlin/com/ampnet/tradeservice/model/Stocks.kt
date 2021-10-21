package com.ampnet.tradeservice.model

data class Stock(
    val id: Int,
    val name: String,
    val symbol: String,
    val price: Double,
    val priceChange24h: Double
)

data class Stocks(
    val stocks: List<Stock>
)
