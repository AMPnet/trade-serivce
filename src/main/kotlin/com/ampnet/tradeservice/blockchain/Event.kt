package com.ampnet.tradeservice.blockchain

import com.ampnet.tradeservice.generated.contract.Events
import java.math.BigInteger

data class Event(
    val chainId: Long,
    val type: EventType,
    val wallet: String? = null,
    val stockId: String? = null,
    val amount: BigInteger? = null,
    val timestamp: BigInteger? = null
) {
    constructor(chainId: Long, event: Events.BuyOrderCreatedEventResponse) : this(
        chainId, EventType.BUY, event.wallet, event.stockId, event.amount, event.timestamp
    )

    constructor(chainId: Long, event: Events.SellOrderCreatedEventResponse) : this(
        chainId, EventType.SELL, event.wallet, event.stockId, event.amount, event.timestamp
    )

    constructor(chainId: Long, event: Events.OrderSettledEventResponse) : this(
        chainId, EventType.SETTLE
    )
}

enum class EventType {
    BUY, SELL, SETTLE
}
