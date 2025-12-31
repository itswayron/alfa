package dev.weg.alfa.security.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.security.models.role.RoleRequest
import dev.weg.alfa.security.models.role.RoleResponse
import dev.weg.alfa.security.services.RoleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.ROLE)
class RoleController(
    private val service: RoleService
) {

    @PostMapping
    fun createRole(
        @RequestBody request: RoleRequest
    ): ResponseEntity<RoleResponse> {
        val response = service.createRole(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping
    fun getRoles(): ResponseEntity<List<RoleResponse>> {
        val response = service.getRoles()
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updateRole(
        @PathVariable id: Int,
        @RequestBody request: RoleRequest
    ): ResponseEntity<RoleResponse> {
        val response = service.updateRole(id, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteRole(@RequestParam id: Int): ResponseEntity<Void> {
        service.deleteRole(id)
        return ResponseEntity.noContent().build()
    }
}
