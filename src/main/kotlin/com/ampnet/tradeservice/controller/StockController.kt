package com.ampnet.tradeservice.controller

import com.ampnet.tradeservice.model.BlockchainOrderId
import com.ampnet.tradeservice.model.BuyOrder
import com.ampnet.tradeservice.model.CurrentPrice
import com.ampnet.tradeservice.model.SellOrder
import com.ampnet.tradeservice.model.Stocks
import com.ampnet.tradeservice.service.InteractiveBrokersApiService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.BigInteger

@RestController
class StockController(private val apiService: InteractiveBrokersApiService) {

    @GetMapping("/stocks/list")
    fun listStocks(): Stocks {
        return apiService.listStocks()
    }

    @GetMapping("/stocks/{stockId}/current-price")
    fun listPrices(@PathVariable stockId: Int): CurrentPrice {
        return apiService.currentPrice(stockId)
    }

    // used to manually test buy orders
    @PostMapping("/stocks/buy/{stockId}/{amountUsdc}")
    fun buy(@PathVariable stockId: Int, @PathVariable amountUsdc: Double) {
        apiService.placeBuyOrder(
            BuyOrder(stockId, BlockchainOrderId(BigInteger.ZERO), BigDecimal(amountUsdc))
        )
    }

    // used to manually test sell orders
    @PostMapping("/stocks/sell/{stockId}/{numShares}")
    fun sell(@PathVariable stockId: Int, @PathVariable numShares: Int) {
        apiService.placeSellOrder(
            SellOrder(stockId, BlockchainOrderId(BigInteger.ZERO), numShares)
        )
    }
}
