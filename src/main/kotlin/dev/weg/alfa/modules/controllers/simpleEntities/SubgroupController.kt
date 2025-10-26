    package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.services.simpleEntities.SubgroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.SUBGROUP)
class SubGroupController(private val service: SubgroupService) {

    @PostMapping
    fun createSubGroup(@RequestBody request: NameRequest): ResponseEntity<Subgroup> {
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createSubgroup(request))
        return response
    }

    @GetMapping
    fun getAllSubGroups(): ResponseEntity<List<Subgroup>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllSubgroups())
        return response
    }

    @PutMapping("/{id}")
    fun updateSubGroups(@PathVariable id: Int, @RequestBody request: NameRequest): ResponseEntity<Subgroup> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.editSubgroup(Pair(id, request)))
        return response
    }

    @DeleteMapping( "/{id}")
    fun deleteSubGroup(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteSubGroupById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
