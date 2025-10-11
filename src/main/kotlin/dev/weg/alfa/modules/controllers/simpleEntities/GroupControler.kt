package dev.weg.alfa.modules.controllers.simpleEntities


import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.services.simpleEntities.GroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.GROUP)
class GroupController(private val service: GroupService) {

    @PostMapping
    fun createGroup(@RequestBody request: NameRequest): ResponseEntity<Group> {
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createGroup(request))
        return response
    }

    @GetMapping
    fun getAllGroups(): ResponseEntity<List<Group>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllGroup())
        return response
    }

    @PutMapping("/{id}")
    fun updateGroups(@PathVariable id: Int, @RequestBody request: NameRequest): ResponseEntity<Group> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.editGroup(Pair(id, request)))
        return response
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteGroupById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

}