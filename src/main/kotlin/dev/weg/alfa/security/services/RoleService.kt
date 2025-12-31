package dev.weg.alfa.security.services

import dev.weg.alfa.modules.repositories.user.UserRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import dev.weg.alfa.security.models.role.*
import dev.weg.alfa.security.repositories.RoleRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

// TODO: Add logging, custom exceptions, validations and tests
@Service
class RoleService(
    private val repository: RoleRepository,
    private val userRepository: UserRepository
) {

    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    fun createRole(request: RoleRequest): RoleResponse {
        if (repository.existsByName(request.name)) {
            throw IllegalArgumentException("Role with name ${request.name} already exists")
        }

        validatePermissionDependencies(request.permissions)
        val role = Role(
            name = request.name,
            permissions = request.permissions
        )

        val saved = repository.save(role)

        return RoleResponse(
            id = saved.id,
            name = saved.name,
            permissions = saved.permissions
        )
    }

    @PreAuthorize("hasAuthority('VIEW_ROLES')")
    fun getRoles(): List<RoleResponse> {
        return repository.findAll().map { role ->
            RoleResponse(
                id = role.id,
                name = role.name,
                permissions = role.permissions
            )
        }
    }

    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    fun updateRole(id: Int, request: RoleRequest): RoleResponse {
        val role = repository.findByIdOrThrow(id)

        if (role.name != request.name && repository.existsByName(request.name)) {
            throw IllegalArgumentException("Role with name ${request.name} already exists")
        }

        validatePermissionDependencies(request.permissions)
        val updatedRole = role.copy(
            name = request.name,
            permissions = request.permissions
        )
        val saved = repository.save(updatedRole)

        return RoleResponse(
            id = saved.id,
            name = saved.name,
            permissions = saved.permissions
        )
    }

    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    fun deleteRole(id: Int) {
        if (userRepository.existsByRoleId(id)) {
            throw IllegalArgumentException("Cannot delete role with id $id because it is assigned to one or more users")
            // TODO: Create custom exception
        }

        repository.deleteById(id)
    }

    private fun validatePermissionDependencies(permissions: Set<Permission>) {
        permissions.forEach { p ->
            val required = PermissionPolicy.getDependencies(p)

            val missing = required - permissions

            if (missing.isNotEmpty()) {
                throw IllegalArgumentException("Missing required permissions: $missing for permission: $p")
            }
        }
    }
}
