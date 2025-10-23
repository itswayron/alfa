package dev.weg.alfa.modules.repositories.businessPartner

import dev.weg.alfa.modules.exceptions.ExceptionProvider
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Repository

@Repository
class BusinessPartnerRepositoryImpl : ExceptionProvider<Int> {
    override fun notFoundException(id: Int) = EntityNotFoundException("Parceiro id: $id não encontrado!")
}
