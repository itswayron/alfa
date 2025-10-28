package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.services.simpleEntities.SectorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.SECTOR)
class SectorController (private val service: SectorService){
    @PostMapping
    fun createSector(@RequestBody request: NameRequest): ResponseEntity<Sector>{
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createSector(request))
        return response
    }
    @GetMapping
    fun getAllGroups(): ResponseEntity<List<Sector>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllSectors())
        return response
    }

    @PutMapping("/{id}")
    fun updateGroups(@PathVariable id: Int, @RequestBody request: NameRequest): ResponseEntity<Sector> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.editSector(Pair(id, request)))
        return response
    }
    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteSectorById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

}