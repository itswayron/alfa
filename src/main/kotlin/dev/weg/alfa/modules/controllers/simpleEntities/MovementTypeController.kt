package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.movement.MovementType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.MOVEMENT_TYPES)
class MovementTypeController {

    @GetMapping
    fun getAllMovementType(): ResponseEntity<List<MovementType>> {
        val types = MovementType.entries.toList()
        return ResponseEntity(types, HttpStatus.OK)
    }
}
