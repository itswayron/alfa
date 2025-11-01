package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderPatch
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderRequest
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderResponse
import dev.weg.alfa.modules.services.ProductionOrderService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.PRODUCTION_ORDER)
class ProductionOrderController(private val service: ProductionOrderService) {
    @PostMapping
    fun createProductionOrder(@RequestBody request: ProductionOrderRequest): ResponseEntity<ProductionOrderResponse> {
        val response = service.createProductionOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getProductionOrderById(@PathVariable id: Int): ResponseEntity<ProductionOrderResponse> {
        val response = service.getProductionOrderById(id)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping
    fun getProductionOrders(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "date") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<ProductionOrderResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
        val response = service.getProductionOrders(pageable)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{id}")
    fun updateProductionOrder(
        @PathVariable id: Int, @RequestBody patch: ProductionOrderPatch
    ): ResponseEntity<ProductionOrderResponse> {
        val response = service.updateProductionOrder(Pair(id, patch))
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProductionOrder(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteProductionOrder(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
