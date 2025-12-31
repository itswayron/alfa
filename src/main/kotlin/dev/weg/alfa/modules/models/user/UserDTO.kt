package dev.weg.alfa.modules.models.user

import dev.weg.alfa.security.models.role.RoleResponse
import java.time.LocalDateTime

data class UserRequest(
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val roleId: Int,
)

data class UserResponse(
    val id: Int,
    val username: String,
    val name: String? = null,
    val profilePath: String? = null,
    val email: String,
    val createdAt: LocalDateTime,
    val role: RoleResponse,
)
