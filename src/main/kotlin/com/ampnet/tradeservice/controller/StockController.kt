package com.ampnet.tradeservice.controller

import com.ampnet.tradeservice.model.Stock
import com.ampnet.tradeservice.model.Stocks
import com.ampnet.tradeservice.service.InteractiveBrokersApiService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class StockController(private val apiService: InteractiveBrokersApiService) {

    @GetMapping("/stocks")
    fun listStocks(): Stocks {
        return apiService.listStocks()
    }

    @GetMapping("/stocks/{stockId}")
    fun listStock(@PathVariable stockId: Int): Stock {
        return apiService.listStock(stockId)
    }
}
