package dev.weg.alfa.modules.exceptions

import jakarta.persistence.EntityNotFoundException

interface ExceptionProvider<ID> {
  fun notFoundException(id: ID): EntityNotFoundException
}
