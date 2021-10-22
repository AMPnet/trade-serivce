package com.ampnet.tradeservice.model

import java.math.BigDecimal

data class BuyOrder(
    val stockId: Int,
    val amountUsd: BigDecimal
)

data class SellOrder(
    val stockId: Int,
    val numShares: Int
)

data class PlacedBuyOrder(
    val orderId: Int,
    val stockId: Int,
    val amountUsd: BigDecimal,
    val maxPrice: Double,
    val numShares: Int
)

data class PlacedSellOrder(
    val orderId: Int,
    val stockId: Int,
    val minPrice: Double,
    val numShares: Int
)
