package dev.weg.alfa.infra.audit.services

import com.fasterxml.jackson.databind.ObjectMapper
import dev.weg.alfa.infra.audit.model.*
import dev.weg.alfa.infra.audit.repository.AuditRepository
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.repositories.user.UserRepository
import dev.weg.alfa.modules.repositories.utils.getCurrentUser
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class AuditService(
    private val repository: AuditRepository,
    private val userRepository: UserRepository,
    private val mapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun save(action: String, diff: AuditDiff) {
        val user = userRepository.getCurrentUser()

        val audit = Audit(
            actor = user.usernameField,
            action = action.trim().uppercase(),
            before = diff.before.toJson(),
            after = diff.after.toJson(),
        )
        repository.save(audit)
    }

    @PreAuthorize("hasAuthority('VIEW_AUDITS')")
    fun getAll(filter: AuditFilter, pageable: Pageable): PageDTO<Audit> {
        logger.info("Fetching filtered Audits with filter={}, pageable={}", filter, pageable)

        val specs = filter.toSpecification()
        val page = repository.findAll(specs, pageable)

        logger.debug(
            "Found {} matching records out of total {} audits",
            page.numberOfElements,
            repository.count()
        )

        val pageDTO = page.toDTO()

        logger.info(
            "Returning filtered Audit page with {} elements (page {}/{})",
            pageDTO.content.size, pageDTO.currentPage + 1, pageDTO.totalPages
        )
        return pageDTO
    }

    private fun AuditPayload?.toJson(): String? = this?.let { mapper.writeValueAsString(it) }
}
