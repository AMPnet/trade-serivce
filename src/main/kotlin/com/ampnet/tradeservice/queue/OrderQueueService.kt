package com.ampnet.tradeservice.queue

import com.ampnet.tradeservice.configuration.OrderProperties
import com.ampnet.tradeservice.model.BuyOrder
import com.ampnet.tradeservice.model.SellOrder
import com.ampnet.tradeservice.repository.OrderRepository
import com.ampnet.tradeservice.service.InteractiveBrokersApiService
import com.ampnet.tradeservice.service.OrderSettlementService
import mu.KLogging
import org.jobrunr.scheduling.BackgroundJob
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class OrderQueueService(
    private val orderProperties: OrderProperties,
    private val orderRepository: OrderRepository,
    private val orderSettlementService: OrderSettlementService,
    private val interactiveBrokersApiService: InteractiveBrokersApiService
) {

    companion object : KLogging()

    init {
        BackgroundJob.scheduleRecurrently("fail-long-pending-orders", "*/60 * * * * *") {
            failLongPendingOrders(orderProperties.maxPendingDuration)
        }

        BackgroundJob.scheduleRecurrently("resubmit-prepared-orders", "*/60 * * * * *") {
            resubmitPreparedOrders(orderProperties.maxPreparedDuration)
        }
    }

    private fun failLongPendingOrders(minAge: Duration) {
        orderRepository.getPendingBuyOrders(minAge).forEach {
            logger.info { "Failing long pending buy order: ${it.serialId}" }
            orderSettlementService.refundOrder(it)
        }

        orderRepository.getPendingSellOrders(minAge).forEach {
            logger.info { "Failing long pending sell order: ${it.serialId}" }
            orderSettlementService.refundOrder(it)
        }
    }

    private fun resubmitPreparedOrders(minAge: Duration) {
        orderRepository.getPreparedBuyOrders(minAge).forEach {
            orderRepository.deleteOrder(it.serialId)
            val buyOrder = BuyOrder(
                stockId = it.order.stockId,
                blockchainOrderId = it.order.blockchainOrderId,
                chainId = it.order.chainId,
                wallet = it.order.wallet,
                amountUsd = it.order.amountUsd
            )
            interactiveBrokersApiService.placeBuyOrder(buyOrder)
            logger.info { "Re-submitted buy order: $buyOrder" }
        }

        orderRepository.getPreparedSellOrders(minAge).forEach {
            orderRepository.deleteOrder(it.serialId)
            val sellOrder = SellOrder(
                stockId = it.order.stockId,
                blockchainOrderId = it.order.blockchainOrderId,
                chainId = it.order.chainId,
                wallet = it.order.wallet,
                numShares = it.order.numShares
            )
            interactiveBrokersApiService.placeSellOrder(sellOrder)
            logger.info { "Re-submitted sell order: $sellOrder" }
        }
    }
}
