package dev.weg.alfa.security.repositories

import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.security.models.password.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Int> {
  fun findByToken(token: String): PasswordResetToken?
  fun deleteByUser(user: User)
}
