package com.ampnet.tradeservice.model

import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicReference

@JvmInline
value class InteractiveBrokersOrderId(val value: Int)

@JvmInline
value class BlockchainOrderId(val value: BigInteger)

data class BuyOrder(
    val stockId: Int,
    val blockchainOrderId: BlockchainOrderId,
    val amountUsd: BigDecimal
)

data class SellOrder(
    val stockId: Int,
    val blockchainOrderId: BlockchainOrderId,
    val numShares: Int
)

sealed interface PlacedOrder

data class PlacedBuyOrder(
    val interactiveBrokersOrderId: InteractiveBrokersOrderId,
    val blockchainOrderId: BlockchainOrderId,
    val stockId: Int,
    val amountUsd: BigDecimal,
    val maxPrice: Double,
    val numShares: Int
) : PlacedOrder

data class PlacedSellOrder(
    val interactiveBrokersOrderId: InteractiveBrokersOrderId,
    val blockchainOrderId: BlockchainOrderId,
    val stockId: Int,
    val minPrice: Double,
    val numShares: Int
) : PlacedOrder

enum class OrderStatus {
    PREPARED, PENDING, SUCCESSFUL, FAILED
}

sealed interface QueuedOrder {
    val status: AtomicReference<OrderStatus>
    val order: PlacedOrder
}

class QueuedBuyOrder(
    override val status: AtomicReference<OrderStatus>,
    override val order: PlacedBuyOrder
) : QueuedOrder

class QueuedSellOrder(
    override val status: AtomicReference<OrderStatus>,
    override val order: PlacedSellOrder
) : QueuedOrder
