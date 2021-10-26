package com.ampnet.tradeservice.service.ib.wrappers

import com.ib.client.TickAttrib
import com.ib.client.TickAttribBidAsk
import com.ib.client.TickAttribLast
import com.ib.client.TickType
import com.ib.controller.ConcurrentHashSet
import com.ib.partial.ETicker
import mu.KLogging
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
@Suppress("TooManyFunctions")
class LoggingTickerWrapper : ETicker {

    companion object : KLogging()

    private val registeredTickers = ConcurrentHashSet<Int>()
    private val currentStockPrices = ConcurrentHashMap<Int, Double>()
    private val initialStockPrices = ConcurrentHashMap<Int, CompletableFuture<Double>>()

    fun registerTicker(tickerId: Int): Boolean {
        val tickerAdded = registeredTickers.add(tickerId)

        if (tickerAdded) {
            logger.info { "Register ticker with id: $tickerId" }
            val future = CompletableFuture<Double>()
            initialStockPrices[tickerId] = future
        } else {
            logger.info { "Ticker already exists for id: $tickerId" }
        }

        return tickerAdded
    }

    fun currentPrice(tickerId: Int, timeout: Long, timeUnit: TimeUnit): Double {
        return currentStockPrices[tickerId] ?: initialStockPrices[tickerId]?.get(timeout, timeUnit) ?: 0.0
    }

    override fun tickPrice(tickerId: Int, field: Int, price: Double, attrib: TickAttrib?) {
        if (
            field == TickType.DELAYED_BID.index() ||
            field == TickType.DELAYED_ASK.index() ||
            field == TickType.DELAYED_LAST.index()
        ) {
            logger.info { "tickPrice(tickerId: $tickerId, field: $field, price: $price, attrib: $attrib)" }
            if (currentStockPrices[tickerId] == null) {
                initialStockPrices[tickerId]?.complete(price)
            }
            currentStockPrices[tickerId] = price
        }
    }

    override fun tickSize(tickerId: Int, field: Int, size: Int) {
        // logger.info { "tickSize(tickerId: $tickerId, field: $field, size: $size)" }
    }

    override fun tickOptionComputation(
        tickerId: Int,
        field: Int,
        impliedVol: Double,
        delta: Double,
        optPrice: Double,
        pvDividend: Double,
        gamma: Double,
        vega: Double,
        theta: Double,
        undPrice: Double
    ) {
        logger.info {
            "tickOptionComputation(tickerId: $tickerId, field: $field, impliedVol: $impliedVol, delta: $delta," +
                " optPrice: $optPrice, pvDividend: $pvDividend, gamma: $gamma, vega: $vega, theta: $theta," +
                " undPrice: $undPrice)"
        }
    }

    override fun tickGeneric(tickerId: Int, tickType: Int, value: Double) {
        logger.info { "tickGeneric(tickerId: $tickerId, tickType: $tickType, value: $value)" }
    }

    override fun tickString(tickerId: Int, tickType: Int, value: String?) {
        // logger.info { "tickString(tickerId: $tickerId, tickType: $tickType, value: $value)" }
    }

    override fun tickEFP(
        tickerId: Int,
        tickType: Int,
        basisPoints: Double,
        formattedBasisPoints: String?,
        impliedFuture: Double,
        holdDays: Int,
        futureLastTradeDate: String?,
        dividendImpact: Double,
        dividendsToLastTradeDate: Double
    ) {
        logger.info {
            "tickEFP(tickerId: $tickerId, tickType: $tickType, basisPoints: $basisPoints," +
                " formattedBasisPoints: $formattedBasisPoints, impliedFuture: $impliedFuture," +
                " holdDays: $holdDays, futureLastTradeDate: $futureLastTradeDate," +
                " dividendImpact: $dividendImpact, dividendsToLastTradeDate: $dividendsToLastTradeDate)"
        }
    }

    override fun tickByTickAllLast(
        reqId: Int,
        tickType: Int,
        time: Long,
        price: Double,
        size: Int,
        tickAttribLast: TickAttribLast?,
        exchange: String?,
        specialConditions: String?
    ) {
        logger.info {
            "tickByTickAllLast(reqId: $reqId, tickType: $tickType, time: $time, price: $price, size: $size," +
                " tickAttribLast: $tickAttribLast, exchange: $exchange, specialConditions: $specialConditions)"
        }
    }

    override fun tickByTickBidAsk(
        reqId: Int,
        time: Long,
        bidPrice: Double,
        askPrice: Double,
        bidSize: Int,
        askSize: Int,
        tickAttribBidAsk: TickAttribBidAsk?
    ) {
        logger.info {
            "tickByTickBidAsk(reqId: $reqId, time: $time, bidPrice: $bidPrice, askPrice: $askPrice," +
                " bidSize: $bidSize, askSize: $askSize, tickAttribBidAsk: $tickAttribBidAsk)"
        }
    }

    override fun tickByTickMidPoint(reqId: Int, time: Long, midPoint: Double) {
        logger.info { "tickByTickMidPoint(reqId: $reqId, time: $time, midPoint: $midPoint)" }
    }

    override fun tickNews(
        tickerId: Int,
        timeStamp: Long,
        providerCode: String?,
        articleId: String?,
        headline: String?,
        extraData: String?
    ) {
        logger.info {
            "tickNews(tickerId: $tickerId, timeStamp: $timeStamp, providerCode: $providerCode, articleId: $articleId," +
                " headline: $headline, extraData: $extraData)"
        }
    }

    override fun tickReqParams(tickerId: Int, minTick: Double, bboExchange: String?, snapshotPermissions: Int) {
        logger.info {
            "tickReqParams(tickerId: $tickerId, minTick: $minTick, bboExchange: $bboExchange," +
                " snapshotPermissions: $snapshotPermissions)"
        }
    }
}
