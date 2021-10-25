package com.ampnet.tradeservice.queue

import com.ampnet.tradeservice.blockchain.BlockchainEventService
import com.ampnet.tradeservice.blockchain.BlockchainService
import com.ampnet.tradeservice.blockchain.EventType
import com.ampnet.tradeservice.blockchain.properties.Chain
import com.ampnet.tradeservice.blockchain.properties.ChainPropertiesHandler
import com.ampnet.tradeservice.configuration.ChainProperties
import com.ampnet.tradeservice.model.BuyOrder
import com.ampnet.tradeservice.model.SellOrder
import com.ampnet.tradeservice.repository.PollingTaskRepository
import com.ampnet.tradeservice.service.InteractiveBrokersApiService
import mu.KotlinLogging
import org.jobrunr.scheduling.BackgroundJob
import org.springframework.stereotype.Service
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class QueueService(
    chainPropertiesHandler: ChainPropertiesHandler,
    private val blockchainService: BlockchainService,
    private val blockchainEventService: BlockchainEventService,
    private val taskRepository: PollingTaskRepository,
    private val interactiveBrokersApiService: InteractiveBrokersApiService
) {
    init {
        val activeChains = Chain.values().mapNotNull { chain ->
            chainPropertiesHandler.getChainProperties(chain)?.let { properties ->
                Pair(chain, properties)
            }
        }
        activeChains.forEach {
            BackgroundJob.scheduleRecurrently(it.first.name, "*/10 * * * * *") {
                processTask(it.first, it.second)
            }
        }
    }

    fun processTask(chain: Chain, chainProperties: ChainProperties) {
        val task = taskRepository.getTaskForChainId(chain.id)
        val startBlockNumber = task?.blockNumber?.let { it + 1 } ?: chainProperties.startBlockNumber
        try {
            val latestBlockNumber = blockchainService.getBlockNumber(chain.id)
            val endBlockNumber = calculateEndBlockNumber(
                startBlockNumber, latestBlockNumber.toLong(), chainProperties
            )
            if (startBlockNumber >= endBlockNumber) {
//                logger.debug { "End block: $endBlockNumber is smaller than start block: $startBlockNumber" }
                return
            }
            val events = blockchainEventService.getAllEvents(startBlockNumber, endBlockNumber, chain.id).forEach {
                when (it.type) {
                    EventType.BUY -> {
                        val buyOrder = BuyOrder(
                            stockId = it.stockId?.toInt()!!,
                            blockchainOrderId = it.orderId!!,
                            amountUsd = it.amount?.toUsdcDecimalAmount()!!
                        )
                        interactiveBrokersApiService.placeBuyOrder(buyOrder)
                        // TODO order has been placed above, do we need to do anything else here?
                    }
                    EventType.SELL -> {
                        val sellOrder = SellOrder(
                            stockId = it.stockId?.toInt()!!,
                            blockchainOrderId = it.orderId!!,
                            numShares = it.amount?.toSharesAmount()!!
                        )
                        interactiveBrokersApiService.placeSellOrder(sellOrder)
                        // TODO order has been placed above, do we need to do anything else here?
                    }
                    EventType.SETTLE -> TODO("settle is completed, do something")
                }
            }
            taskRepository.updateTaskForChainId(chain.id, endBlockNumber, Instant.now().toEpochMilli())
        } catch (ex: Throwable) {
            logger.error { "Failed to fetch blockchain events: ${ex.message}" }
        }
    }

    private fun calculateEndBlockNumber(
        startBlockNumber: Long,
        latestBlockNumber: Long,
        chainProperties: ChainProperties
    ): Long =
        if (
            (latestBlockNumber - chainProperties.numOfConfirmations - startBlockNumber) > chainProperties.maxBlocks
        ) {
            startBlockNumber + chainProperties.maxBlocks
        } else {
            latestBlockNumber - chainProperties.numOfConfirmations
        }
}
