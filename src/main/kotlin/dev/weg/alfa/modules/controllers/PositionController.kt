package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.position.PositionCreationRequest
import dev.weg.alfa.modules.models.position.PositionUpdateRequest
import dev.weg.alfa.modules.services.PositionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.POSITION)
class PositionController (private val service: PositionService){

    @PostMapping
    fun createService(@RequestBody Request: PositionCreationRequest): ResponseEntity<Position> {
        val positionCreated = service.createPosition(Request)
        return ResponseEntity(positionCreated, HttpStatus.CREATED)
    }
    @GetMapping
    fun getAllPositions(): ResponseEntity<List<Position>> {
        val positions = service.getAllPositions()
        return ResponseEntity(positions, HttpStatus.OK)
    }
    @GetMapping("/search/{floor}")
    fun getPositionsByFloor(@RequestParam floor: String): ResponseEntity<List<Position>> {
        val positions = service.getPositionByFloor(floor)
        return ResponseEntity(positions, HttpStatus.OK)
    }
    @GetMapping("/search/{side}")
    fun getPositionsBySide(@RequestParam side: String): ResponseEntity<List<Position>> {
        val positions = service.getPositionBySide(side)
        return ResponseEntity(positions, HttpStatus.OK)
    }
    @GetMapping("/search/{Column}")
    fun getPositionsByColumn(@RequestParam column: String): ResponseEntity<List<Position>> {
        val positions = service.getPositionByColumn(column)
        return ResponseEntity(positions, HttpStatus.OK)
    }
    @GetMapping("/search/{floor}")
    fun getPositionsByBox(@RequestParam box: String): ResponseEntity<List<Position>> {
        val positions = service.getPositionByBox(box)
        return ResponseEntity(positions, HttpStatus.OK)
    }
    @PatchMapping("/{id}")
    fun updatePosition(@PathVariable id: Int,@RequestBody request: PositionUpdateRequest): ResponseEntity<Position> {
        val response = service.updatePositions(Pair(id,request))
        return ResponseEntity.ok(response)
    }
    @DeleteMapping("/{id}")
    fun deletePositionById(@PathVariable id: Int): ResponseEntity<Void> {
        service.deletePositionById(id)
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }
}
