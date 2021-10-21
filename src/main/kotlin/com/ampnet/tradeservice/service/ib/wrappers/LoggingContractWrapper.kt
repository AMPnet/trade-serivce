package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.client.ContractDetails
import com.ib.partial.EContract
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class LoggingContractWrapper : EContract {

    companion object : KLogging()

    override fun contractDetails(reqId: Int, contractDetails: ContractDetails?) {
        logger.info { "contractDetails(reqId: $reqId, contractDetails: $contractDetails)" }
    }

    override fun bondContractDetails(reqId: Int, contractDetails: ContractDetails?) {
        logger.info { "bondContractDetails(reqId: $reqId, contractDetails: $contractDetails)" }
    }

    override fun contractDetailsEnd(reqId: Int) {
        logger.info { "contractDetailsEnd(reqId: $reqId)" }
    }
}
