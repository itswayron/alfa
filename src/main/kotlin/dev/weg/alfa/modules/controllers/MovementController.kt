package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.movement.MovementPatch
import dev.weg.alfa.modules.models.movement.MovementRequest
import dev.weg.alfa.modules.models.movement.MovementResponse
import dev.weg.alfa.modules.services.MovementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun getAllMovements(): ResponseEntity<List<MovementResponse>> {
        val response = service.getAllMovements()
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
