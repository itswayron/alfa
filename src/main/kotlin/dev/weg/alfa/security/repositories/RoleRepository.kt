package dev.weg.alfa.security.repositories

import dev.weg.alfa.security.models.role.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Int> {
    fun existsByName(name: String): Boolean
    fun findByName(name: String): Role?
}
