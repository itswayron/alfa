package dev.weg.alfa.security.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.security.models.role.PermissionScopeDTO
import dev.weg.alfa.security.services.PermissionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.PERMISSION)
class PermissionController(private val service: PermissionService) {

    @GetMapping
    fun listPermissions(): List<PermissionScopeDTO> = service.listAll()
}
