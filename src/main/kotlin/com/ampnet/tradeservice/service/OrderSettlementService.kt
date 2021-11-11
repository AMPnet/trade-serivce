package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.blockchain.Amount
import com.ampnet.tradeservice.blockchain.BlockchainService
import com.ampnet.tradeservice.model.PlacedBuyOrder
import com.ampnet.tradeservice.model.PlacedOrder
import com.ampnet.tradeservice.model.PlacedSellOrder
import com.ampnet.tradeservice.model.SerialId
import com.ampnet.tradeservice.repository.OrderRepository
import mu.KLogging
import org.springframework.stereotype.Service
import java.math.BigInteger
import kotlin.math.ceil

@Service
class OrderSettlementService(
    private val blockchainService: BlockchainService,
    private val orderRepository: OrderRepository
) {

    companion object : KLogging()

    fun pendingOrder(placedOrder: SerialId<out PlacedOrder>) {
        orderRepository.markAsPending(placedOrder)
    }

    fun refundOrder(placedOrder: SerialId<out PlacedOrder>) {
        blockchainService.settle(
            chainId = placedOrder.order.chainId,
            orderId = placedOrder.order.blockchainOrderId.value,
            usdAmount = BigInteger.ZERO,
            tokenAmount = BigInteger.ZERO,
            wallet = placedOrder.order.wallet
        )
        orderRepository.markAsFailed(placedOrder)
    }

    @Suppress("MagicNumber")
    fun settleBuyOrder(placedOrder: SerialId<PlacedBuyOrder>, averageFillPrice: Double) {
        val amountPaid = placedOrder.order.numShares * averageFillPrice
        val roundedAmountPaid = (ceil(amountPaid * 100).toLong() / 100.0).toBigDecimal()
        logger.info { "Amount paid: $roundedAmountPaid, average fill price: $averageFillPrice" }

        blockchainService.settle(
            chainId = placedOrder.order.chainId,
            orderId = placedOrder.order.blockchainOrderId.value,
            usdAmount = Amount.fromUsdcDecimalAmount(roundedAmountPaid).value,
            tokenAmount = Amount.fromSharesAmount(placedOrder.order.numShares).value,
            wallet = placedOrder.order.wallet
        )
        orderRepository.markAsSuccessful(placedOrder)
    }

    @Suppress("MagicNumber")
    fun settleSellOrder(placedOrder: SerialId<PlacedSellOrder>, averageFillPrice: Double) {
        val amountReceived = placedOrder.order.numShares * averageFillPrice
        val roundedAmountReceived = (ceil(amountReceived * 100).toLong() / 100.0).toBigDecimal()
        logger.info { "Amount received: $roundedAmountReceived, average fill price: $averageFillPrice" }

        blockchainService.settle(
            chainId = placedOrder.order.chainId,
            orderId = placedOrder.order.blockchainOrderId.value,
            usdAmount = Amount.fromUsdcDecimalAmount(roundedAmountReceived).value,
            tokenAmount = Amount.fromSharesAmount(placedOrder.order.numShares).value,
            wallet = placedOrder.order.wallet
        )
        orderRepository.markAsSuccessful(placedOrder)
    }
}
