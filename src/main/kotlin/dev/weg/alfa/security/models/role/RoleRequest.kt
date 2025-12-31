package dev.weg.alfa.security.models.role

data class RoleRequest(
    val name: String,
    val permissions: Set<Permission> = emptySet()
)

data class RoleResponse(
    val id: Int,
    val name: String,
    val permissions: Set<Permission> = emptySet()
)
