package com.ampnet.tradeservice.controller

import com.ampnet.tradeservice.model.CurrentPrices
import com.ampnet.tradeservice.model.Stocks
import com.ampnet.tradeservice.service.InteractiveBrokersApiService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StockController(private val apiService: InteractiveBrokersApiService) {

    @GetMapping("/stocks/list")
    fun listStocks(): Stocks {
        return apiService.listStocks()
    }

    @GetMapping("/stocks/current-prices")
    fun listPrices(): CurrentPrices {
        return apiService.currentPrices()
    }
}
