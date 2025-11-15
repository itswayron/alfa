package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.movement.MovementFilter
import dev.weg.alfa.modules.models.movement.MovementPatch
import dev.weg.alfa.modules.models.movement.MovementRequest
import dev.weg.alfa.modules.models.movement.MovementResponse
import dev.weg.alfa.modules.services.MovementService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping(ApiRoutes.MOVEMENT)
class MovementController(private val service: MovementService) {
    @PostMapping
    fun createMovement(@RequestBody request: MovementRequest): ResponseEntity<MovementResponse> {
        val response = service.createMovement(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getMovementById(@PathVariable id: Int): ResponseEntity<MovementResponse> {
        val response = service.getMovementById(id)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    fun getAllMovements(
        @RequestParam(required = false) stockId: Int?,
        @RequestParam(required = false) batchId: Int?,
        @RequestParam(required = false) typeId: Int?,
        @RequestParam(required = false) statusId: Int?,
        @RequestParam(required = false) sectorId: Int?,
        @RequestParam(required = false) employeeId: Int?,
        @RequestParam(required = false) observation: String?,
        @RequestParam(required = false) dateFrom: LocalDateTime?,
        @RequestParam(required = false) dateTo: LocalDateTime?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "date") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<MovementResponse>> {
        val filter = MovementFilter(stockId, batchId, typeId, statusId, sectorId, employeeId, observation, dateFrom, dateTo)
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))

        val response = service.getAllMovements(filter, pageable)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updateMovement(
        @PathVariable id: Int,
        @RequestBody patch: MovementPatch
    ): ResponseEntity<MovementResponse> {
        val response = service.updateMovement(id, patch)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteMovement(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteMovement(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
