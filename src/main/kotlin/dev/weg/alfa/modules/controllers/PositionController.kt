package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.position.PositionRequest
import dev.weg.alfa.modules.models.position.PositionPatch
import dev.weg.alfa.modules.services.PositionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.POSITION)
class PositionController (private val service: PositionService){

    @PostMapping
    fun createService(@RequestBody request: PositionRequest): ResponseEntity<Position> {
        val positionCreated = service.createPosition(request)
        return ResponseEntity(positionCreated, HttpStatus.CREATED)
    }
    @GetMapping
    fun getAllPositions(): ResponseEntity<List<Position>> {
        val positions = service.getAllPositions()
        return ResponseEntity(positions, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updatePosition(@PathVariable id: Int,@RequestBody request: PositionPatch): ResponseEntity<Position> {
        val response = service.updatePosition(Pair(id,request))
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deletePositionById(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deletePositionById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
