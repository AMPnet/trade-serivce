package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.model.BuyOrder
import com.ampnet.tradeservice.model.CurrentPrice
import com.ampnet.tradeservice.model.CurrentPrices
import com.ampnet.tradeservice.model.PlacedBuyOrder
import com.ampnet.tradeservice.model.PlacedSellOrder
import com.ampnet.tradeservice.model.SellOrder
import com.ampnet.tradeservice.model.Stock
import com.ampnet.tradeservice.model.Stocks
import com.ampnet.tradeservice.service.ib.contracts.PredefinedContracts
import com.ampnet.tradeservice.service.ib.contracts.PredefinedContracts.toContract
import com.ampnet.tradeservice.service.ib.wrappers.LoggingContractWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingOrderWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingTickerWrapper
import com.ib.client.Contract
import com.ib.client.Order
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor

@Service
@Suppress("MagicNumber") // TODO we don't care for now
class InteractiveBrokersApiService(
    private val connectionService: InteractiveBrokersConnectionService,
    private val correlationService: InteractiveBrokersCorrelationService,
    private val contractWrapper: LoggingContractWrapper,
    private val tickerWrapper: LoggingTickerWrapper,
    private val orderWrapper: LoggingOrderWrapper
) {

    companion object : KLogging()

    fun listStocks(): Stocks {
        logger.info { "Request stocks list" }
        val contracts = PredefinedContracts.contracts().map { Pair(it, correlationService.nextRequestId()) }
        val futures = contracts.map { Pair(contractWrapper.registerRequest(it.second), it.second) }

        for ((contract, requestId) in contracts) {
            connectionService.client.reqContractDetails(requestId, contract)
        }

        val retrievedContractsDetails = futures.map {
            logger.info { "Block on id: ${it.second}" }
            it.first.get(5, TimeUnit.SECONDS)
        }

        for (contractDetails in retrievedContractsDetails) {
            registerTicker(contractDetails.contract())
        }

        val stocks = retrievedContractsDetails.map {
            Stock(
                id = it.conid(),
                name = it.longName(),
                symbol = it.contract().symbol(),
                price = tickerWrapper.currentPrice(it.conid(), 5, TimeUnit.SECONDS),
                priceChange24h = 0.0 // TODO set price
            )
        }

        return Stocks(stocks)
    }

    fun currentPrices(): CurrentPrices {
        logger.info { "Request prices list" }
        val contracts = PredefinedContracts.contracts()

        for (contract in contracts) {
            registerTicker(contract)
        }

        val currentPrices = contracts.map {
            CurrentPrice(
                stockId = it.conid(),
                price = tickerWrapper.currentPrice(it.conid(), 5, TimeUnit.SECONDS),
                priceChange24h = 0.0 // TODO
            )
        }

        return CurrentPrices(currentPrices)
    }

    fun placeBuyOrder(buyOrder: BuyOrder): PlacedBuyOrder {
        logger.info { "Request buy order: $buyOrder" }
        val contract = buyOrder.stockId.toContract()

        registerTicker(contract)

        val maxPrice = calcPrice(contract, 0.025) // buy at max. 2.5% higher price
        val numShares = floor(buyOrder.amountUsd.toDouble() / maxPrice).toInt()
        logger.info { "Buying $numShares of ${contract.symbol()}" }

        val order = Order().apply {
            action("BUY")
            orderType("LMT")
            totalQuantity(numShares.toDouble())
            lmtPrice(maxPrice)
        }
        val orderId = orderWrapper.nextOrderId()

        connectionService.client.placeOrder(orderId, contract, order)

        return PlacedBuyOrder(
            orderId = orderId,
            stockId = buyOrder.stockId,
            amountUsd = buyOrder.amountUsd,
            maxPrice = maxPrice,
            numShares = numShares
        )
    }

    fun placeSellOrder(sellOrder: SellOrder): PlacedSellOrder {
        logger.info { "Request sell order: $sellOrder" }
        val contract = sellOrder.stockId.toContract()

        registerTicker(contract)

        val minPrice = calcPrice(contract, -0.025) // sell at min. 2.5% lower price
        logger.info { "Selling ${sellOrder.numShares} of ${contract.symbol()}" }

        val order = Order().apply {
            action("SELL")
            orderType("LMT")
            totalQuantity(sellOrder.numShares.toDouble())
            lmtPrice(minPrice)
        }
        val orderId = orderWrapper.nextOrderId()

        connectionService.client.placeOrder(orderId, contract, order)

        return PlacedSellOrder(
            orderId = orderId,
            stockId = sellOrder.stockId,
            minPrice = minPrice,
            numShares = sellOrder.numShares
        )
    }

    private fun registerTicker(contract: Contract) {
        // only request market data for tickers which are not active yet
        if (tickerWrapper.registerTicker(contract.conid())) {
            connectionService.client.reqMktData(
                contract.conid(),
                contract,
                "",
                false,
                false,
                emptyList()
            )
        }
    }

    private fun calcPrice(contract: Contract, adjustment: Double): Double {
        val currentPrice = tickerWrapper.currentPrice(contract.conid(), 5, TimeUnit.SECONDS)
        logger.info { "Current price for ${contract.symbol()}: $currentPrice" }
        val adjustedPrice = currentPrice * (1.0 + adjustment)
        logger.info { "Adjusted price for ${contract.symbol()}: $adjustedPrice" }
        val roundedPrice = ceil(adjustedPrice * 100).toLong() / 100.0 // round to two decimals
        logger.info { "Rounded price for ${contract.symbol()}: $roundedPrice" }
        return roundedPrice
    }
}
