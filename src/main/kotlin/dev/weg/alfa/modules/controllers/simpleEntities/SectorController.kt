package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.services.simpleEntities.SectorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.SECTOR)
class SectorController(private val service: SectorService) {
    @PostMapping
    fun createSector(@RequestBody request: NameRequest): ResponseEntity<Sector> {
        val response = service.createSector(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllGroups(): ResponseEntity<List<Sector>> {
        val response = service.getAllSectors()
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PutMapping("/{id}")
    fun updateGroups(@PathVariable id: Int, @RequestBody request: NameRequest): ResponseEntity<Sector> {
        val response = service.editSector(Pair(id, request))
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteSectorById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
