package dev.weg.alfa.modules.repositories.item

import dev.weg.alfa.modules.exceptions.ExceptionProvider
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class ItemRepositoryImpl: ExceptionProvider<Int> {
    override fun notFoundException(id: Int): EntityNotFoundException = EntityNotFoundException("Item id: $id não encontrado!")
}
