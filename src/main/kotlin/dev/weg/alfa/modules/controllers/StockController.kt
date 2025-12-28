package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.compositeItem.CompositeItemRequest
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.stock.StockFilter
import dev.weg.alfa.modules.models.stock.StockPatch
import dev.weg.alfa.modules.models.stock.StockRequest
import dev.weg.alfa.modules.models.stock.StockResponse
import dev.weg.alfa.modules.services.CompositeItemCreationService
import dev.weg.alfa.modules.services.StockService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.STOCK)
class StockController(
    private val service: StockService,
    private val compositeItemCreationService: CompositeItemCreationService
) {
    @PostMapping
    fun createStock(@RequestBody request: StockRequest): ResponseEntity<StockResponse> {
        val response = service.createStock(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getStockById(@PathVariable id: Int): ResponseEntity<StockResponse> {
        val response = service.getStockById(id)
        return ResponseEntity(response, HttpStatus.OK)
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
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/filter")
    fun getFilteredStocks(
        @RequestParam(required = false) text: String?,
        @RequestParam(required = false) groupId: Int?,
        @RequestParam(required = false) subgroupId: Int?,
        @RequestParam(required = false) supplierId: Int?,
        @RequestParam(required = false) isActive: Boolean? = true,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<StockResponse>> {
        val filter = StockFilter(text, groupId, subgroupId, supplierId, isActive)
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.fromString(direction), sort)
        )

        val response = service.getFilteredStocks(filter, pageable)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updateStock(@PathVariable id: Int, @RequestBody patch: StockPatch): ResponseEntity<StockResponse> {
        val response = service.updateStock(id, patch)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteStock(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteStock(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PostMapping("/composite")
    fun createCompositeItem(@RequestBody request: CompositeItemRequest): ResponseEntity<StockResponse> {
        val response = compositeItemCreationService.execute(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
