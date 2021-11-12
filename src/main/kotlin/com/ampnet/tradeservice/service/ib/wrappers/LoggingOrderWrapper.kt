package com.ampnet.tradeservice.service.ib.wrappers

import com.ampnet.tradeservice.model.OrderStatus
import com.ampnet.tradeservice.model.PlacedBuyOrder
import com.ampnet.tradeservice.model.PlacedOrder
import com.ampnet.tradeservice.model.PlacedSellOrder
import com.ampnet.tradeservice.model.QueuedBuyOrder
import com.ampnet.tradeservice.model.QueuedOrder
import com.ampnet.tradeservice.model.QueuedSellOrder
import com.ampnet.tradeservice.model.SerialId
import com.ampnet.tradeservice.service.OrderSettlementService
import com.ib.client.Contract
import com.ib.client.Execution
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.partial.EOrder
import mu.KLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator

@Suppress("TooManyFunctions")
@Component
class LoggingOrderWrapper(
    private val settlementService: OrderSettlementService
) : EOrder {

    companion object : KLogging()

    private val nextOrderId = AtomicInteger()
    private val queuedOrders = ConcurrentHashMap<Int, QueuedOrder>()

    fun nextOrderId() = nextOrderId.getAndIncrement()

    fun queueBuyOrder(order: SerialId<PlacedBuyOrder>) {
        queuedOrders[order.order.interactiveBrokersOrderId.value] =
            QueuedBuyOrder(AtomicReference(OrderStatus.PREPARED), order)
    }

    fun queueSellOrder(order: SerialId<PlacedSellOrder>) {
        queuedOrders[order.order.interactiveBrokersOrderId.value] =
            QueuedSellOrder(AtomicReference(OrderStatus.PREPARED), order)
    }

    override fun nextValidId(orderId: Int) {
        logger.info { "nextValidId(orderId: $orderId)" }
        nextOrderId.set(orderId)
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
        logger.info { "Got order status: $status (order id = $orderId)" }
        when (status) {
            "PendingSubmit", "PendingCancel", "PreSubmitted", "Submitted" ->
                queuedOrders[orderId]?.let {
                    it.status.updateAndGet(orderStatusTransitions(it.order, avgFillPrice, OrderStatus.PENDING))
                }
            "ApiCancelled", "Cancelled", "Inactive" ->
                queuedOrders[orderId]?.let {
                    it.status.updateAndGet(orderStatusTransitions(it.order, avgFillPrice, OrderStatus.FAILED))
                }
            "Filled" ->
                queuedOrders[orderId]?.let {
                    it.status.updateAndGet(orderStatusTransitions(it.order, avgFillPrice, OrderStatus.SUCCESSFUL))
                }
            else ->
                queuedOrders[orderId]?.let {
                    it.status.updateAndGet(orderStatusTransitions(it.order, avgFillPrice, OrderStatus.FAILED))
                }
        }
    }

    override fun openOrder(orderId: Int, contract: Contract?, order: Order?, orderState: OrderState?) {
        logger.info { "Open order, status: ${orderState?.status} (order id = $orderId)" }
        when (orderState?.status) {
            "PendingSubmit", "PendingCancel", "PreSubmitted", "Submitted" ->
                queuedOrders[orderId]?.let {
                    it.status.updateAndGet(orderStatusTransitions(it.order, 0.0, OrderStatus.PENDING))
                }
            "ApiCancelled", "Cancelled", "Inactive" ->
                queuedOrders[orderId]?.let {
                    it.status.updateAndGet(orderStatusTransitions(it.order, 0.0, OrderStatus.FAILED))
                }
            else -> {
            }
        }
    }

    override fun openOrderEnd() {
        logger.info { "openOrderEnd()" }
    }

    override fun execDetails(reqId: Int, contract: Contract?, execution: Execution?) {
        logger.info { "execDetails(reqId: $reqId, contract: $contract, execution: $execution)" }
    }

    override fun execDetailsEnd(reqId: Int) {
        logger.info { "execDetailsEnd(reqId: $reqId)" }
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

    private fun orderStatusTransitions(
        placedOrder: SerialId<out PlacedOrder>,
        averageFillPrice: Double,
        targetStatus: OrderStatus
    ): UnaryOperator<OrderStatus> {
        return UnaryOperator { oldStatus ->
            when (oldStatus) {
                OrderStatus.FAILED, OrderStatus.SUCCESSFUL -> oldStatus // do nothing for completed orders
                OrderStatus.PREPARED, OrderStatus.PENDING ->
                    when (targetStatus) {
                        // do not allow transition back to prepared status
                        OrderStatus.PREPARED, OrderStatus.PENDING -> {
                            logger.info { "Order pending: ${placedOrder.serialId}" }
                            settlementService.pendingOrder(placedOrder)
                            OrderStatus.PENDING
                        }

                        OrderStatus.FAILED -> {
                            logger.info { "Order failed: ${placedOrder.serialId}" }
                            settlementService.refundOrder(placedOrder)
                            OrderStatus.FAILED
                        }

                        OrderStatus.SUCCESSFUL -> {
                            logger.info { "Order successful: ${placedOrder.serialId}" }

                            when (placedOrder.order) {
                                is PlacedBuyOrder -> settlementService.settleBuyOrder(
                                    SerialId(
                                        placedOrder.serialId,
                                        placedOrder.status,
                                        placedOrder.order
                                    ),
                                    averageFillPrice
                                )
                                is PlacedSellOrder -> settlementService.settleSellOrder(
                                    SerialId(
                                        placedOrder.serialId,
                                        placedOrder.status,
                                        placedOrder.order
                                    ),
                                    averageFillPrice
                                )
                            }

                            OrderStatus.SUCCESSFUL
                        }
                    }
            }
        }
    }
}
