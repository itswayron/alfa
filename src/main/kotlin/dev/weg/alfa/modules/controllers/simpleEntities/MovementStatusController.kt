package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.MovementStatus.MovementStatusRequest
import dev.weg.alfa.modules.models.MovementStatus.MovementStatusResponse
import dev.weg.alfa.modules.services.simpleEntities.MovimentStatausService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(ApiRoutes.MOVEMENT_STATUS)
class MovementStatusController(private val service: MovimentStatausService) {

    @PostMapping
    fun createMovementStatus(@RequestBody request: MovementStatusRequest): ResponseEntity<MovementStatusResponse> {
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createMovimentStataus(request))
        return response
    }

    @GetMapping
    fun getAllMovementStatus(): ResponseEntity<List<MovementStatusResponse>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllMovimentStataus())
        return response
    }

    @PutMapping("/{id}")
    fun updateMovementStatus(
        @PathVariable id: Int,
        @RequestBody request: MovementStatusRequest
    ): ResponseEntity<MovementStatusResponse> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.editMovimentStataus(id, request))
        return response
    }

    @DeleteMapping("/{id}")
    fun deleteMovementStatus(@PathVariable id: Int): ResponseEntity<Void> {
        service.deleteMovimentStatausById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
