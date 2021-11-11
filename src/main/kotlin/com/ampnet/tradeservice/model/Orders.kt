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
    val chainId: Long,
    val wallet: String,
    val amountUsd: BigDecimal
)

data class SellOrder(
    val stockId: Int,
    val blockchainOrderId: BlockchainOrderId,
    val chainId: Long,
    val wallet: String,
    val numShares: Int
)

sealed interface PlacedOrder {
    val blockchainOrderId: BlockchainOrderId
    val chainId: Long
    val wallet: String
}

data class PlacedBuyOrder(
    val interactiveBrokersOrderId: InteractiveBrokersOrderId,
    override val blockchainOrderId: BlockchainOrderId,
    override val chainId: Long,
    override val wallet: String,
    val stockId: Int,
    val amountUsd: BigDecimal,
    val maxPrice: Double,
    val numShares: Int
) : PlacedOrder

data class PlacedSellOrder(
    val interactiveBrokersOrderId: InteractiveBrokersOrderId,
    override val blockchainOrderId: BlockchainOrderId,
    override val chainId: Long,
    override val wallet: String,
    val stockId: Int,
    val minPrice: Double,
    val numShares: Int
) : PlacedOrder

data class SerialId<O : PlacedOrder>(
    val serialId: Int,
    val status: OrderStatus,
    val order: O
)

enum class OrderStatus {
    PREPARED, PENDING, SUCCESSFUL, FAILED
}

sealed interface QueuedOrder {
    val status: AtomicReference<OrderStatus>
    val order: SerialId<out PlacedOrder>
}

class QueuedBuyOrder(
    override val status: AtomicReference<OrderStatus>,
    override val order: SerialId<PlacedBuyOrder>
) : QueuedOrder

class QueuedSellOrder(
    override val status: AtomicReference<OrderStatus>,
    override val order: SerialId<PlacedSellOrder>
) : QueuedOrder
