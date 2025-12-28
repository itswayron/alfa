package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.movement.MovementStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.MOVEMENT_STATUS)
class MovementStatusController {

    @GetMapping
    fun getAllMovementStatus(): ResponseEntity<List<MovementStatus>> {
        val types = MovementStatus.entries.toList()
        return ResponseEntity(types, HttpStatus.OK)
    }
}
