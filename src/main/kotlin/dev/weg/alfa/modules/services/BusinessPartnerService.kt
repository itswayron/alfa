package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.businessPartner.BusinessPartnerPatch
import dev.weg.alfa.modules.repositories.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BusinessPartnerService(
    private val repository: BusinessPartnerRepository,
    //private val validator: Validator<BusinessPartner>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createPartner(request: BusinessPartner): BusinessPartner {
        val sanitizedRequest = request.sanitized()
        logger.info("Creating business partner: ${sanitizedRequest.name}")

        //validator.validate(sanitizedRequest)
        val newPartner = repository.save(sanitizedRequest)

        logger.info("Business partner ${newPartner.name} created with id: ${newPartner.id}")
        return newPartner
    }

    fun findPartnerById(id: Int): BusinessPartner {
        logger.info("Fetching partner with id: $id")
        val partner = repository.findByIdOrThrow(id)
        logger.info("Retrieved partner with ID: $id - Name: ${partner.name}")
        return partner
    }

    fun findAllPartners(): List<BusinessPartner> {
        logger.info("Finding all partners")
        val partners = repository.findAll()
        logger.info("Fetched ${partners.size} partners.")
        return partners
    }

    fun updatePartner(command: Pair<Int, BusinessPartnerPatch>): BusinessPartner {
        val (id, newPartner) = command
        logger.info("Updating partner with $id with name: ${newPartner.name}.")
        val oldPartner = repository.findByIdOrThrow(id)
        val updatedPartner = BusinessPartner(
            id = oldPartner.id,
            name = newPartner.name ?: oldPartner.name  ,
            cnpj = newPartner.cnpj ?: oldPartner.cnpj,
            relation = newPartner.relation ?: oldPartner.relation
        )
        repository.save(updatedPartner)
        logger.info("Partner name updated to ${newPartner.name}")
        return updatedPartner
    }

    fun deletePartner(id: Int) {
        logger.info("Deleting partner with id: $id")
        repository.deleteById(id)
        logger.info("Partner deleted")
    }

    private fun BusinessPartner.sanitized(): BusinessPartner =
        BusinessPartner(
            name = this.name.trim(),
            cnpj = this.cnpj.trim(),
            relation = this.relation.trim(),
        )
}
