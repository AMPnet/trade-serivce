package com.ampnet.tradeservice.controller

import com.ampnet.tradeservice.repository.DummyRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DummyController(private val repository: DummyRepository) {

    @GetMapping("/dummy/{id}")
    fun getByUuid(@PathVariable id: Int): Int? {
        return repository.getById(id)?.dummyColumn
    }
}
