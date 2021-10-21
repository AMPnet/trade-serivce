package com.ampnet.tradeservice.model

data class CurrentPrice(
    val stockId: Int,
    val price: Double,
    val priceChange24h: Double
)

data class CurrentPrices(
    val prices: List<CurrentPrice>
)
