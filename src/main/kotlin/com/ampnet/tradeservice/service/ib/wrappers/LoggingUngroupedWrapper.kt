package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.client.Bar
import com.ib.client.CommissionReport
import com.ib.client.Contract
import com.ib.client.ContractDescription
import com.ib.client.ContractDetails
import com.ib.client.DeltaNeutralContract
import com.ib.client.DepthMktDataDescription
import com.ib.client.Execution
import com.ib.client.FamilyCode
import com.ib.client.HistogramEntry
import com.ib.client.HistoricalTick
import com.ib.client.HistoricalTickBidAsk
import com.ib.client.HistoricalTickLast
import com.ib.client.NewsProvider
import com.ib.client.PriceIncrement
import com.ib.client.SoftDollarTier
import com.ib.partial.EUngrouped
import mu.KLogging
import org.springframework.stereotype.Component

@Component
@Suppress("TooManyFunctions")
class LoggingUngroupedWrapper : EUngrouped {

    companion object : KLogging()

    override fun execDetails(reqId: Int, contract: Contract?, execution: Execution?) {
        logger.info { "execDetails(reqId: $reqId, contract: $contract, execution: $execution)" }
    }

    override fun execDetailsEnd(reqId: Int) {
        logger.info { "execDetailsEnd(reqId: $reqId)" }
    }

    override fun updateMktDepth(tickerId: Int, position: Int, operation: Int, side: Int, price: Double, size: Int) {
        logger.info {
            "updateMktDepth(tickerId: $tickerId, position: $position, operation: $operation, side: $side," +
                " price: $price, size: $size)"
        }
    }

    override fun updateMktDepthL2(
        tickerId: Int,
        position: Int,
        marketMaker: String?,
        operation: Int,
        side: Int,
        price: Double,
        size: Int,
        isSmartDepth: Boolean
    ) {
        logger.info {
            "updateMktDepthL2(tickerId: $tickerId, position: $position, marketMaker: $marketMaker," +
                " operation: $operation, side: $side, price: $price, size: $size, isSmartDepth: $isSmartDepth)"
        }
    }

    override fun updateNewsBulletin(msgId: Int, msgType: Int, message: String?, origExchange: String?) {
        logger.info {
            "updateNewsBulletin(msgId: $msgId, msgType: $msgType, message: $message, origExchange: $origExchange)"
        }
    }

    override fun receiveFA(faDataType: Int, xml: String?) {
        logger.info { "receiveFA(faDataType: $faDataType, xml: $xml)" }
    }

    override fun historicalData(reqId: Int, bar: Bar?) {
        logger.info { "historicalData(reqId: $reqId, bar: $bar)" }
    }

    override fun scannerParameters(xml: String?) {
        logger.info { "scannerParameters(xml: $xml)" }
    }

    override fun scannerData(
        reqId: Int,
        rank: Int,
        contractDetails: ContractDetails?,
        distance: String?,
        benchmark: String?,
        projection: String?,
        legsStr: String?
    ) {
        logger.info {
            "scannerData(reqId: $reqId, rank: $rank, contractDetails: $contractDetails, distance: $distance," +
                " benchmark: $benchmark, projection: $projection, legsStr: $legsStr)"
        }
    }

    override fun scannerDataEnd(reqId: Int) {
        logger.info { "scannerDataEnd(reqId: $reqId)" }
    }

    override fun realtimeBar(
        reqId: Int,
        time: Long,
        open: Double,
        high: Double,
        low: Double,
        close: Double,
        volume: Long,
        wap: Double,
        count: Int
    ) {
        logger.info {
            "realtimeBar(reqId: $reqId, time: $time, open: $open, high: $high, low: $low, close: $close," +
                " volume: $volume, wap: $wap, count: $count)"
        }
    }

    override fun currentTime(time: Long) {
        logger.info { "currentTime(time: $time)" }
    }

    override fun fundamentalData(reqId: Int, data: String?) {
        logger.info { "fundamentalData(reqId: $reqId, data: $data)" }
    }

    override fun deltaNeutralValidation(reqId: Int, deltaNeutralContract: DeltaNeutralContract?) {
        logger.info { "deltaNeutralValidation(reqId: $reqId, deltaNeutralContract: $deltaNeutralContract)" }
    }

    override fun tickSnapshotEnd(reqId: Int) {
        logger.info { "tickSnapshotEnd(reqId: $reqId)" }
    }

    override fun marketDataType(reqId: Int, marketDataType: Int) {
        logger.info { "marketDataType(reqId: $reqId, marketDataType: $marketDataType)" }
    }

    override fun commissionReport(commissionReport: CommissionReport?) {
        logger.info { "commissionReport(commissionReport: $commissionReport)" }
    }

    override fun position(account: String?, contract: Contract?, pos: Double, avgCost: Double) {
        logger.info { "position(account: $account, contract: $contract, pos: $pos, avgCost: $avgCost)" }
    }

    override fun positionEnd() {
        logger.info { "positionEnd()" }
    }

    override fun verifyMessageAPI(apiData: String?) {
        logger.info { "verifyMessageAPI(apiData: $apiData)" }
    }

    override fun verifyCompleted(isSuccessful: Boolean, errorText: String?) {
        logger.info { "verifyCompleted(isSuccessful: $isSuccessful, errorText: $errorText)" }
    }

    override fun verifyAndAuthMessageAPI(apiData: String?, xyzChallenge: String?) {
        logger.info { "verifyAndAuthMessageAPI(apiData: $apiData, xyzChallenge: $xyzChallenge)" }
    }

    override fun verifyAndAuthCompleted(isSuccessful: Boolean, errorText: String?) {
        logger.info { "verifyAndAuthCompleted(isSuccessful: $isSuccessful, errorText: $errorText)" }
    }

    override fun displayGroupList(reqId: Int, groups: String?) {
        logger.info { "displayGroupList(reqId: $reqId, groups: $groups)" }
    }

    override fun displayGroupUpdated(reqId: Int, contractInfo: String?) {
        logger.info { "displayGroupUpdated(reqId: $reqId, contractInfo: $contractInfo)" }
    }

    override fun positionMulti(
        reqId: Int,
        account: String?,
        modelCode: String?,
        contract: Contract?,
        pos: Double,
        avgCost: Double
    ) {
        logger.info {
            "positionMulti(reqId: $reqId, account: $account, modelCode: $modelCode, contract: $contract," +
                " pos: $pos, avgCost: $avgCost)"
        }
    }

    override fun positionMultiEnd(reqId: Int) {
        logger.info { "positionMultiEnd(reqId: $reqId)" }
    }

    override fun securityDefinitionOptionalParameter(
        reqId: Int,
        exchange: String?,
        underlyingConId: Int,
        tradingClass: String?,
        multiplier: String?,
        expirations: MutableSet<String>?,
        strikes: MutableSet<Double>?
    ) {
        logger.info {
            "securityDefinitionOptionalParameter(reqId: $reqId, exchange: $exchange," +
                " underlyingConId: $underlyingConId, tradingClass: $tradingClass, multiplier: $multiplier," +
                " expirations: $expirations, strikes: $strikes)"
        }
    }

    override fun securityDefinitionOptionalParameterEnd(reqId: Int) {
        logger.info { "securityDefinitionOptionalParameterEnd(reqId: $reqId)" }
    }

    override fun softDollarTiers(reqId: Int, tiers: Array<out SoftDollarTier>?) {
        logger.info { "softDollarTiers(reqId: $reqId, tiers: $tiers)" }
    }

    override fun familyCodes(familyCodes: Array<out FamilyCode>?) {
        logger.info { "familyCodes(familyCodes: $familyCodes)" }
    }

    override fun symbolSamples(reqId: Int, contractDescriptions: Array<out ContractDescription>?) {
        logger.info { "symbolSamples(reqId: $reqId, contractDescriptions: $contractDescriptions)" }
    }

    override fun historicalDataEnd(reqId: Int, startDateStr: String?, endDateStr: String?) {
        logger.info { "historicalDataEnd(reqId: $reqId, startDateStr: $startDateStr, endDateStr: $endDateStr)" }
    }

    override fun mktDepthExchanges(depthMktDataDescriptions: Array<out DepthMktDataDescription>?) {
        logger.info { "mktDepthExchanges(depthMktDataDescriptions: $depthMktDataDescriptions)" }
    }

    override fun smartComponents(reqId: Int, theMap: MutableMap<Int, MutableMap.MutableEntry<String, Char>>?) {
        logger.info { "smartComponents(reqId: $reqId, theMap: $theMap, MutableMap.MutableEntry<String, Char>>?)" }
    }

    override fun newsProviders(newsProviders: Array<out NewsProvider>?) {
        logger.info { "newsProviders(newsProviders: $newsProviders)" }
    }

    override fun newsArticle(requestId: Int, articleType: Int, articleText: String?) {
        logger.info { "newsArticle(requestId: $requestId, articleType: $articleType, articleText: $articleText)" }
    }

    override fun historicalNews(
        requestId: Int,
        time: String?,
        providerCode: String?,
        articleId: String?,
        headline: String?
    ) {
        logger.info {
            "historicalNews(requestId: $requestId, time: $time, providerCode: $providerCode," +
                " articleId: $articleId, headline: $headline)"
        }
    }

    override fun historicalNewsEnd(requestId: Int, hasMore: Boolean) {
        logger.info { "historicalNewsEnd(requestId: $requestId, hasMore: $hasMore)" }
    }

    override fun headTimestamp(reqId: Int, headTimestamp: String?) {
        logger.info { "headTimestamp(reqId: $reqId, headTimestamp: $headTimestamp)" }
    }

    override fun histogramData(reqId: Int, items: MutableList<HistogramEntry>?) {
        logger.info { "histogramData(reqId: $reqId, items: $items)" }
    }

    override fun historicalDataUpdate(reqId: Int, bar: Bar?) {
        logger.info { "historicalDataUpdate(reqId: $reqId, bar: $bar)" }
    }

    override fun rerouteMktDataReq(reqId: Int, conId: Int, exchange: String?) {
        logger.info { "rerouteMktDataReq(reqId: $reqId, conId: $conId, exchange: $exchange)" }
    }

    override fun rerouteMktDepthReq(reqId: Int, conId: Int, exchange: String?) {
        logger.info { "rerouteMktDepthReq(reqId: $reqId, conId: $conId, exchange: $exchange)" }
    }

    override fun marketRule(marketRuleId: Int, priceIncrements: Array<out PriceIncrement>?) {
        logger.info { "marketRule(marketRuleId: $marketRuleId, priceIncrements: $priceIncrements)" }
    }

    override fun pnl(reqId: Int, dailyPnL: Double, unrealizedPnL: Double, realizedPnL: Double) {
        logger.info {
            "pnl(reqId: $reqId, dailyPnL: $dailyPnL, unrealizedPnL: $unrealizedPnL, realizedPnL: $realizedPnL)"
        }
    }

    override fun pnlSingle(
        reqId: Int,
        pos: Int,
        dailyPnL: Double,
        unrealizedPnL: Double,
        realizedPnL: Double,
        value: Double
    ) {
        logger.info {
            "pnlSingle(reqId: $reqId, pos: $pos, dailyPnL: $dailyPnL, unrealizedPnL: $unrealizedPnL," +
                " realizedPnL: $realizedPnL, value: $value)"
        }
    }

    override fun historicalTicks(reqId: Int, ticks: MutableList<HistoricalTick>?, done: Boolean) {
        logger.info { "historicalTicks(reqId: $reqId, ticks: $ticks, done: $done)" }
    }

    override fun historicalTicksBidAsk(reqId: Int, ticks: MutableList<HistoricalTickBidAsk>?, done: Boolean) {
        logger.info { "historicalTicksBidAsk(reqId: $reqId, ticks: $ticks, done: $done)" }
    }

    override fun historicalTicksLast(reqId: Int, ticks: MutableList<HistoricalTickLast>?, done: Boolean) {
        logger.info { "historicalTicksLast(reqId: $reqId, ticks: $ticks, done: $done)" }
    }
}
