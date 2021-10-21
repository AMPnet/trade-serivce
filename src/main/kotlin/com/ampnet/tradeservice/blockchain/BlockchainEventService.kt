package com.ampnet.tradeservice.blockchain

import com.ampnet.tradeservice.blockchain.properties.ChainPropertiesHandler
import com.ampnet.tradeservice.blockchain.properties.ChainPropertiesWithServices
import com.ampnet.tradeservice.exception.ErrorCode
import com.ampnet.tradeservice.exception.InternalException
import com.ampnet.tradeservice.generated.contract.Events
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.response.EthLog
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import kotlin.jvm.Throws

private val logger = KotlinLogging.logger {}

@Service
class BlockchainEventService(private val chainPropertiesHandler: ChainPropertiesHandler) {

    @Throws(InternalException::class)
    fun getAllEvents(startBlockNumber: Long, endBlockNumber: Long, chainId: Long): List<Event> {
        val chainProperties = chainPropertiesHandler.getBlockchainProperties(chainId)
        val contract = chainProperties.chain.orderBookAddress

        val ethFilter = EthFilter(
            DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlockNumber)),
            DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlockNumber)),
            contract
        )
        val ethLog: EthLog = chainProperties.web3j.ethGetLogs(ethFilter).sendSafely()
            ?: throw InternalException(
                ErrorCode.BLOCKCHAIN_JSON_RPC,
                "Failed to fetch events from $startBlockNumber to $endBlockNumber block, " +
                        "for OrderBook contract: $contract"
            )
        val logs = ethLog.logs.mapNotNull { it.get() as? EthLog.LogObject }
        return generateEvents(logs, chainProperties, chainId)
    }

    private fun generateEvents(
        logs: List<Log>,
        chainProperties: ChainPropertiesWithServices,
        chainId: Long
    ): List<Event> {
        val events = mutableListOf<Event>()
        val txReceipt = TransactionReceipt().apply { this.logs = logs }
        // The contract address is not important since it doesn't fetch anything from the blockchain.
        // It is only used to map logs to events.
        val contract = Events.load(
            chainProperties.chain.callerAddress,
            chainProperties.web3j,
            chainProperties.credentials,
            DefaultGasProvider()
        )
        skipException { contract.getBuyOrderCreatedEvents(txReceipt) }?.forEach {
            events.add(Event(chainId, it))
        }
        skipException { contract.getSellOrderCreatedEvents(txReceipt) }?.forEach {
            events.add(Event(chainId, it))
        }
        skipException { contract.getOrderSettledEvents(txReceipt) }?.forEach {
            events.add(Event(chainId, it))
        }
        return events
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun <T> skipException(action: () -> T): T? {
        return try {
            action()
        } catch (ex: Exception) {
            logger.debug { "There was an exception while fetching events: ${ex.message}" }
            null
        }
    }
}
