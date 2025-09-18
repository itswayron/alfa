package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.exceptions.user.UserNotFoundException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl() : UserRepositoryCustom {
  override fun notFoundException(id: String): EntityNotFoundException = UserNotFoundException("User with $id not found")
}
