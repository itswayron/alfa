package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.simpleModels.MovementType
import dev.weg.alfa.modules.services.simpleEntities.MovementTypeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.MOVEMENT_TYPES)
class MovementTypeController (private val service: MovementTypeService ){

    @GetMapping
    fun getAllMovementType(): ResponseEntity<List<MovementType>>{
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllMovementTypes())
        return response
    }

}