package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.client.Contract
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.partial.EOrder
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class LoggingOrderWrapper : EOrder {

    companion object : KLogging()

    override fun nextValidId(orderId: Int) {
        logger.info { "nextValidId(orderId: $orderId)" }
    }

    override fun orderStatus(
        orderId: Int,
        status: String?,
        filled: Double,
        remaining: Double,
        avgFillPrice: Double,
        permId: Int,
        parentId: Int,
        lastFillPrice: Double,
        clientId: Int,
        whyHeld: String?,
        mktCapPrice: Double
    ) {
        logger.info {
            "orderStatus(orderId: $orderId, status: $status, filled: $filled, remaining: $remaining," +
                " avgFillPrice: $avgFillPrice, permId: $permId, parentId: $parentId," +
                " lastFillPrice: $lastFillPrice, clientId: $clientId, whyHeld: $whyHeld, mktCapPrice: $mktCapPrice)"
        }
    }

    override fun openOrder(orderId: Int, contract: Contract?, order: Order?, orderState: OrderState?) {
        logger.info { "openOrder(orderId: $orderId, contract: $contract, order: $order, orderState: $orderState)" }
    }

    override fun openOrderEnd() {
        logger.info { "openOrderEnd()" }
    }

    override fun orderBound(orderId: Long, apiClientId: Int, apiOrderId: Int) {
        logger.info { "orderBound(orderId: $orderId, apiClientId: $apiClientId, apiOrderId: $apiOrderId)" }
    }

    override fun completedOrder(contract: Contract?, order: Order?, orderState: OrderState?) {
        logger.info { "completedOrder(contract: $contract, order: $order, orderState: $orderState)" }
    }

    override fun completedOrdersEnd() {
        logger.info { "completedOrdersEnd()" }
    }
}
