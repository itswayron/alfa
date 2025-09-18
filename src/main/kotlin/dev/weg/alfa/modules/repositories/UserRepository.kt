package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>, UserRepositoryCustom {
  fun findByUsernameField(username: String): User?
  fun findByEmail(email: String): User?
  fun existsByEmail(email: String): Boolean
  fun existsByUsernameField(username: String): Boolean
}
