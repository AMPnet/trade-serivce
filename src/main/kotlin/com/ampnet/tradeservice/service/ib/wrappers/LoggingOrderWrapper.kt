package com.ampnet.tradeservice.service.ib.wrappers

import com.ampnet.tradeservice.blockchain.Amount
import com.ampnet.tradeservice.blockchain.BlockchainService
import com.ampnet.tradeservice.model.OrderStatus
import com.ampnet.tradeservice.model.PlacedBuyOrder
import com.ampnet.tradeservice.model.PlacedOrder
import com.ampnet.tradeservice.model.PlacedSellOrder
import com.ampnet.tradeservice.model.QueuedBuyOrder
import com.ampnet.tradeservice.model.QueuedOrder
import com.ampnet.tradeservice.model.QueuedSellOrder
import com.ib.client.Contract
import com.ib.client.Execution
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.partial.EOrder
import mu.KLogging
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator
import kotlin.math.ceil

@Suppress("TooManyFunctions")
@Component
class LoggingOrderWrapper(private val blockchainService: BlockchainService) : EOrder {

    companion object : KLogging()

    private val nextOrderId = AtomicInteger()
    private val queuedOrders = ConcurrentHashMap<Int, QueuedOrder>()

    private fun orderStatusTransitions(
        placedOrder: PlacedOrder,
        averageFillPrice: Double,
        targetStatus: OrderStatus
    ): UnaryOperator<OrderStatus> {
        return UnaryOperator { oldStatus ->
            when (oldStatus) {
                OrderStatus.FAILED, OrderStatus.SUCCESSFUL -> oldStatus // do nothing for completed orders
                OrderStatus.PREPARED, OrderStatus.PENDING ->
                    when (targetStatus) {
                        // do not allow transition back to prepared status
                        OrderStatus.PREPARED, OrderStatus.PENDING -> OrderStatus.PENDING
                        OrderStatus.FAILED -> {
                            logger.info { "Order failed!" }
                            refundOrder(placedOrder)
                            OrderStatus.FAILED
                        }
                        OrderStatus.SUCCESSFUL -> {
                            logger.info { "Order successful" }

                            when (placedOrder) {
                                is PlacedBuyOrder -> settleBuyOrder(placedOrder, averageFillPrice)
                                is PlacedSellOrder -> settleSellOrder(placedOrder, averageFillPrice)
                            }

                            OrderStatus.SUCCESSFUL
                        }
                    }
            }
        }
    }

    private fun refundOrder(placedOrder: PlacedOrder) {
        blockchainService.settle(
            chainId = placedOrder.chainId,
            orderId = placedOrder.blockchainOrderId.value,
            usdAmount = BigInteger.ZERO,
            tokenAmount = BigInteger.ZERO,
            wallet = placedOrder.wallet
        )
    }

    @Suppress("MagicNumber")
    private fun settleBuyOrder(placedOrder: PlacedBuyOrder, averageFillPrice: Double) {
        val amountPaid = placedOrder.numShares * averageFillPrice
        val roundedAmountPaid = (ceil(amountPaid * 100).toLong() / 100.0).toBigDecimal()
        logger.info { "Amount paid: $roundedAmountPaid, average fill price: $averageFillPrice" }

        blockchainService.settle(
            chainId = placedOrder.chainId,
            orderId = placedOrder.blockchainOrderId.value,
            usdAmount = Amount.fromUsdcDecimalAmount(roundedAmountPaid).value,
            tokenAmount = Amount.fromSharesAmount(placedOrder.numShares).value,
            wallet = placedOrder.wallet
        )
    }

    @Suppress("MagicNumber")
    private fun settleSellOrder(placedOrder: PlacedSellOrder, averageFillPrice: Double) {
        val amountReceived = placedOrder.numShares * averageFillPrice
        val roundedAmountReceived = (ceil(amountReceived * 100).toLong() / 100.0).toBigDecimal()
        logger.info { "Amount received: $roundedAmountReceived, average fill price: $averageFillPrice" }

        blockchainService.settle(
            chainId = placedOrder.chainId,
            orderId = placedOrder.blockchainOrderId.value,
            usdAmount = Amount.fromUsdcDecimalAmount(roundedAmountReceived).value,
            tokenAmount = Amount.fromSharesAmount(placedOrder.numShares).value,
            wallet = placedOrder.wallet
        )
    }

    fun nextOrderId() = nextOrderId.getAndIncrement()

    fun queueBuyOrder(order: PlacedBuyOrder) {
        queuedOrders[order.interactiveBrokersOrderId.value] =
            QueuedBuyOrder(AtomicReference(OrderStatus.PREPARED), order)
    }

    fun queueSellOrder(order: PlacedSellOrder) {
        queuedOrders[order.interactiveBrokersOrderId.value] =
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
}
