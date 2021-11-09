package com.ampnet.tradeservice.blockchain

import com.ampnet.tradeservice.generated.contract.Events
import com.ampnet.tradeservice.model.BlockchainOrderId
import java.math.BigDecimal
import java.math.BigInteger

data class Event(
    val chainId: Long,
    val type: EventType,
    val wallet: String? = null,
    val stockId: String? = null,
    val orderId: BlockchainOrderId? = null,
    val amount: Amount? = null,
    val timestamp: BigInteger? = null
) {
    constructor(chainId: Long, event: Events.BuyOrderCreatedEventResponse) : this(
        chainId,
        EventType.BUY,
        event.wallet,
        event.stockId,
        event.orderId?.let { BlockchainOrderId(it) },
        event.amount?.let { Amount(it) },
        event.timestamp
    )

    constructor(chainId: Long, event: Events.SellOrderCreatedEventResponse) : this(
        chainId,
        EventType.SELL,
        event.wallet,
        event.stockId,
        event.orderId?.let { BlockchainOrderId(it) },
        event.amount?.let { Amount(it) },
        event.timestamp
    )

    constructor(chainId: Long, event: Events.OrderSettledEventResponse) : this(
        chainId, EventType.SETTLE
    )
}

enum class EventType {
    BUY, SELL, SETTLE
}

@JvmInline
value class Amount(val value: BigInteger) {
    companion object {
        val USDC_SCALE = BigDecimal.TEN.pow(18)
        val SHARES_SCALE = BigDecimal.TEN.pow(18)
        fun fromUsdcDecimalAmount(amount: BigDecimal) = Amount(amount.multiply(USDC_SCALE).toBigInteger())
        fun fromSharesAmount(amount: Int) = Amount(amount.toBigDecimal().multiply(SHARES_SCALE).toBigInteger())
    }

    fun toUsdcDecimalAmount() = value.toBigDecimal().div(USDC_SCALE)
    fun toSharesAmount() = value.toBigDecimal().div(SHARES_SCALE).toInt()
}
