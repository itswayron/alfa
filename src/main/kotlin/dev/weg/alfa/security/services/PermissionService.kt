package dev.weg.alfa.security.services

import dev.weg.alfa.security.models.role.Permission
import dev.weg.alfa.security.models.role.PermissionScopeDTO
import dev.weg.alfa.security.models.role.toInfoDTO
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class PermissionService {

    @PreAuthorize("hasAuthority('VIEW_PERMISSIONS')")
    fun listAll(): List<PermissionScopeDTO> {
        val scope = Permission.entries.groupBy { it.scope }

        return scope.map { (scope, permissions) ->
            PermissionScopeDTO(
                scope = scope,
                permissions = permissions.map {
                    it.toInfoDTO()
                }.sortedBy { it.name }
            )
        }.sortedBy { it.scope.name }
    }
}
