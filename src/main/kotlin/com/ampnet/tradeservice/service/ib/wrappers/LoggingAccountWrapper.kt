package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.client.Contract
import com.ib.partial.EAccount
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class LoggingAccountWrapper : EAccount {

    companion object : KLogging()

    override fun updateAccountValue(key: String?, value: String?, currency: String?, accountName: String?) {
        logger.info { "updateAccountValue(key: $key, value: $value, currency: $currency, accountName: $accountName)" }
    }

    override fun updatePortfolio(
        contract: Contract?,
        position: Double,
        marketPrice: Double,
        marketValue: Double,
        averageCost: Double,
        unrealizedPNL: Double,
        realizedPNL: Double,
        accountName: String?
    ) {
        logger.info {
            "updatePortfolio(contract: $contract, position: $position, marketPrice: $marketPrice," +
                    " marketValue: $marketValue, averageCost: $averageCost, unrealizedPNL: $unrealizedPNL," +
                    " realizedPNL: $realizedPNL, accountName: $accountName)"
        }
    }

    override fun updateAccountTime(timeStamp: String?) {
        logger.info { "updateAccountTime(timeStamp: $timeStamp)" }
    }

    override fun accountDownloadEnd(accountName: String?) {
        logger.info { "accountDownloadEnd(accountName: $accountName)" }
    }

    override fun accountSummary(reqId: Int, account: String?, tag: String?, value: String?, currency: String?) {
        logger.info {
            "accountSummary(reqId: $reqId, account: $account, tag: $tag, value: $value, currency: $currency)"
        }
    }

    override fun accountSummaryEnd(reqId: Int) {
        logger.info { "accountSummaryEnd(reqId: $reqId)" }
    }

    override fun accountUpdateMulti(
        reqId: Int,
        account: String?,
        modelCode: String?,
        key: String?,
        value: String?,
        currency: String?
    ) {
        logger.info {
            "accountUpdateMulti(reqId: $reqId, account: $account, modelCode: $modelCode, key: $key, value: $value," +
                    " currency: $currency)"
        }
    }

    override fun accountUpdateMultiEnd(reqId: Int) {
        logger.info { "accountUpdateMultiEnd(reqId: $reqId)" }
    }

    override fun managedAccounts(accountsList: String?) {
        logger.info { "managedAccounts(accountsList: $accountsList)" }
    }
}
