package dev.weg.alfa.modules.repositories.user

import dev.weg.alfa.modules.exceptions.ExceptionProvider
import dev.weg.alfa.modules.models.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int>, ExceptionProvider<Int> {
    fun findByUsernameField(username: String): User?
    fun findByEmailField(email: String): User?
    fun existsByEmailField(email: String): Boolean
    fun existsByUsernameField(username: String): Boolean
    fun existsByRoleId(roleId: Int): Boolean
}
