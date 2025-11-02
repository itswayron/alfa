package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.stock.StockPatch
import dev.weg.alfa.modules.models.stock.StockRequest
import dev.weg.alfa.modules.models.stock.StockResponse
import dev.weg.alfa.modules.services.StockService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.STOCK)
class StockController(private val service: StockService) {

    @PostMapping
    fun createStock(@RequestBody request: StockRequest): ResponseEntity<StockResponse> {
        val response = service.createStock(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getStockById(@PathVariable id: Int): ResponseEntity<StockResponse> {
        val response = service.getStockById(id)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping
    fun getStocks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<StockResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
        val response = service.getStocks(pageable)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{id}")
    fun updateStock(@PathVariable id: Int, @RequestBody patch: StockPatch): ResponseEntity<StockResponse> {
        val response = service.updateStock(id, patch)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteStock(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteStock(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
