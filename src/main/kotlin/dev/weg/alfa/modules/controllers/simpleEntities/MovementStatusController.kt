package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.simpleModels.MovementStatus
import dev.weg.alfa.modules.services.simpleEntities.MovementStatusService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.MOVEMENT_STATUS)
class MovementStatusController(private val service: MovementStatusService) {
    @GetMapping
    fun getAllMovementStatus(): ResponseEntity<List<MovementStatus>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllMovementStatus())
        return response
    }
}
