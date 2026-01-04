package dev.weg.alfa.modules.services

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.PartnerAction
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.businessPartner.BusinessPartnerPatch
import dev.weg.alfa.modules.models.businessPartner.toAuditPayload
import dev.weg.alfa.modules.repositories.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class BusinessPartnerService(
    private val repository: BusinessPartnerRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_PARTNER')")
    @Auditable(action = PartnerAction.CREATED)
    fun createPartner(request: BusinessPartner): BusinessPartner {
        val sanitizedRequest = request.sanitized()
        logger.info("Creating business partner: ${sanitizedRequest.name}")

        val newPartner = repository.save(sanitizedRequest)

        logger.info("Business partner ${newPartner.name} created with id: ${newPartner.id}")
        AuditContext.created(newPartner.toAuditPayload())
        return newPartner
    }

    @PreAuthorize("hasAuthority('VIEW_PARTNER')")
    fun findPartnerById(id: Int): BusinessPartner {
        logger.info("Fetching partner with id: $id")
        val partner = repository.findByIdOrThrow(id)
        logger.info("Retrieved partner with ID: $id - Name: ${partner.name}")
        return partner
    }

    @PreAuthorize("hasAuthority('VIEW_PARTNER')")
    fun findAllPartners(): List<BusinessPartner> {
        logger.info("Finding all partners")
        val partners = repository.findAll()
        logger.info("Fetched ${partners.size} partners.")
        return partners
    }

    @PreAuthorize("hasAuthority('MANAGE_PARTNER')")
    @Auditable(action = PartnerAction.UPDATED)
    fun updatePartner(command: Pair<Int, BusinessPartnerPatch>): BusinessPartner {
        val (id, newPartner) = command
        logger.info("Updating partner with $id with name: ${newPartner.name}.")
        val oldPartner = repository.findByIdOrThrow(id)
        val updatedPartner = BusinessPartner(
            id = oldPartner.id,
            name = newPartner.name ?: oldPartner.name,
            cnpj = newPartner.cnpj ?: oldPartner.cnpj,
            relation = newPartner.relation ?: oldPartner.relation
        )
        val saved = repository.save(updatedPartner)
        logger.info("Partner name updated to ${newPartner.name}")
        AuditContext.updated(oldPartner.toAuditPayload(), saved.toAuditPayload())
        return saved
    }

    @PreAuthorize("hasAuthority('MANAGE_PARTNER')")
    @Auditable(action = PartnerAction.DELETED)
    fun deletePartner(id: Int) {
        logger.info("Deleting partner with id: $id")
        val deleted = repository.findByIdOrThrow(id)
        repository.deleteById(id)
        logger.info("Partner deleted")
        AuditContext.deleted(deleted.toAuditPayload())
    }

    private fun BusinessPartner.sanitized(): BusinessPartner =
        BusinessPartner(
            name = this.name.trim(),
            cnpj = this.cnpj.trim(),
            relation = this.relation.trim(),
        )
}
