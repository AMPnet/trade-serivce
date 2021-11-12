package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.client.ContractDetails
import com.ib.partial.EContract
import mu.KLogging
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future

@Component
class LoggingContractWrapper : EContract {

    companion object : KLogging()

    private val futureContractDetails = ConcurrentHashMap<Int, CompletableFuture<ContractDetails>>()

    fun registerRequest(requestId: Int): Future<ContractDetails> {
        logger.debug { "Register future with id: $requestId" }
        val future = CompletableFuture<ContractDetails>()
        futureContractDetails[requestId] = future
        return future
    }

    override fun contractDetails(reqId: Int, contractDetails: ContractDetails?) {
        logger.debug { "contractDetails(reqId: $reqId, contractDetails: ...)" }
        if (contractDetails != null) {
            val complete = futureContractDetails[reqId]?.complete(contractDetails)
            logger.debug { "Complete future with id: $reqId = $complete" }
        }
    }

    override fun bondContractDetails(reqId: Int, contractDetails: ContractDetails?) {
        logger.debug { "bondContractDetails(reqId: $reqId, contractDetails: $contractDetails)" }
    }

    override fun contractDetailsEnd(reqId: Int) {
        logger.debug { "contractDetailsEnd(reqId: $reqId)" }
    }
}
