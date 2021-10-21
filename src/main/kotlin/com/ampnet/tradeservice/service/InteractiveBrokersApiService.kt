package com.ampnet.tradeservice.service

import com.ampnet.tradeservice.model.CurrentPrice
import com.ampnet.tradeservice.model.CurrentPrices
import com.ampnet.tradeservice.model.Stock
import com.ampnet.tradeservice.model.Stocks
import com.ampnet.tradeservice.service.ib.contracts.PredefinedContracts
import com.ampnet.tradeservice.service.ib.wrappers.LoggingContractWrapper
import com.ampnet.tradeservice.service.ib.wrappers.LoggingTickerWrapper
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
@Suppress("MagicNumber") // TODO we don't care for now
class InteractiveBrokersApiService(
    private val connectionService: InteractiveBrokersConnectionService,
    private val correlationService: InteractiveBrokersCorrelationService,
    private val contractWrapper: LoggingContractWrapper,
    private val tickerWrapper: LoggingTickerWrapper
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
            // only request market data for tickers which are not active yet
            if (tickerWrapper.registerTicker(contractDetails.conid())) {
                connectionService.client.reqMktData(
                    contractDetails.conid(),
                    contractDetails.contract(),
                    "",
                    false,
                    false,
                    emptyList()
                )
            }
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

        val currentPrices = contracts.map {
            CurrentPrice(
                stockId = it.conid(),
                price = tickerWrapper.currentPrice(it.conid(), 5, TimeUnit.SECONDS),
                priceChange24h = 0.0 // TODO
            )
        }

        return CurrentPrices(currentPrices)
    }
}
