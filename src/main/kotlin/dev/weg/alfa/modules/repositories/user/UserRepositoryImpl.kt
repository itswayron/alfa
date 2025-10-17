package dev.weg.alfa.modules.repositories.user

import dev.weg.alfa.modules.exceptions.ExceptionProvider
import dev.weg.alfa.modules.exceptions.user.UserNotFoundException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl() : ExceptionProvider<Int> {
  override fun notFoundException(id: Int): EntityNotFoundException = UserNotFoundException("User with $id not found")
}
