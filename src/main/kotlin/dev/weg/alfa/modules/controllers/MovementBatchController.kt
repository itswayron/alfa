package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.movementBatch.MovementBatchFilter
import dev.weg.alfa.modules.models.movementBatch.MovementBatchPatch
import dev.weg.alfa.modules.models.movementBatch.MovementBatchRequest
import dev.weg.alfa.modules.models.movementBatch.MovementBatchResponse
import dev.weg.alfa.modules.services.MovementBatchService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping(ApiRoutes.MOVEMENT_BATCH)
class MovementBatchController(private val service: MovementBatchService) {
    @PostMapping
    fun createMovementBatch(@RequestBody request: MovementBatchRequest): ResponseEntity<MovementBatchResponse> {
        val response = service.createMovementBatch(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{identifier}")
    fun getBatchByIdentifier(@PathVariable identifier: String): ResponseEntity<MovementBatchResponse> {
        val response = service.getBatchByIdentifier(identifier)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping
    fun getBatches(
        @RequestParam(required = false) code: String?,
        @RequestParam(required = false) document: String?,
        @RequestParam(required = false) partnerId: Int?,
        @RequestParam(required = false) startDate: LocalDateTime?,
        @RequestParam(required = false) endDate: LocalDateTime?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "date") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<MovementBatchResponse>> {
        val filter = MovementBatchFilter(code, document, partnerId, startDate, endDate)
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
        val response = service.getBatches(filter, pageable)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{id}")
    fun updateMovementBatch(
        @PathVariable id: Int, @RequestBody patch: MovementBatchPatch
    ): ResponseEntity<MovementBatchResponse> {
        val response = service.updateMovementBatch(Pair(id, patch))
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteMovementBatchById(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteMovementBatchById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
