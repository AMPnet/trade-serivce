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

    fun failLongPendingOrders(minAge: Duration) {
        logger.info { "Checking for long pending orders..." }
        orderRepository.getPendingBuyOrders(minAge).forEach {
            logger.info { "Failing long pending buy order: ${it.serialId}" }
            orderSettlementService.refundOrder(it)
        }

        orderRepository.getPendingSellOrders(minAge).forEach {
            logger.info { "Failing long pending sell order: ${it.serialId}" }
            orderSettlementService.refundOrder(it)
        }
    }

    fun resubmitPreparedOrders(minAge: Duration) {
        logger.info { "Checking for prepared orders..." }
        orderRepository.getPreparedBuyOrders(minAge).forEach {
            val buyOrder = BuyOrder(
                stockId = it.order.stockId,
                blockchainOrderId = it.order.blockchainOrderId,
                chainId = it.order.chainId,
                wallet = it.order.wallet,
                amountUsd = it.order.amountUsd
            )
            val newId = interactiveBrokersApiService.placeBuyOrder(buyOrder).serialId
            orderRepository.deleteOrder(it.serialId)
            logger.info { "Re-submitted buy order: $buyOrder with new id: $newId" }
        }

        orderRepository.getPreparedSellOrders(minAge).forEach {
            val sellOrder = SellOrder(
                stockId = it.order.stockId,
                blockchainOrderId = it.order.blockchainOrderId,
                chainId = it.order.chainId,
                wallet = it.order.wallet,
                numShares = it.order.numShares
            )
            val newId = interactiveBrokersApiService.placeSellOrder(sellOrder).serialId
            orderRepository.deleteOrder(it.serialId)
            logger.info { "Re-submitted sell order: $sellOrder with new id: $newId" }
        }
    }
}
