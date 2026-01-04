package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.SectorAction
import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.models.simpleModels.audit.toAuditPayload
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class SectorService(private val repository: SectorRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_SECTOR')")
    @Auditable(action = SectorAction.CREATED)
    fun createSector(request: NameRequest): Sector {
        logger.info("Creating Sector with name: ${request.name}")
        val response = repository.save(Sector(name = request.name))
        logger.info("Sector created with id: ${response.id}")
        AuditContext.created(response.toAuditPayload())
        return response
    }

    @PreAuthorize("hasAuthority('VIEW_SECTOR')")
    fun getAllSectors(): List<Sector> {
        logger.info("Retrieving all sectors from the database")
        val response = repository.findAll()
        logger.info("Found ${response.size} sectors on the database")
        return response
    }

    @PreAuthorize("hasAuthority('MANAGE_SECTOR')")
    @Auditable(action = SectorAction.UPDATED)
    fun editSector(command: Pair<Int, NameRequest>): Sector {
        val (id, newSector) = command
        logger.info("Update Sector with $id with name:${newSector.name}")
        val oldSector = repository.findByIdOrThrow(id)
        val response = repository.save(Sector(id = oldSector.id, name = newSector.name))
        logger.info("Sector name update to ${newSector.name}")
        AuditContext.updated(oldSector.toAuditPayload(), response.toAuditPayload())
        return response
    }

    @PreAuthorize("hasAuthority('MANAGE_SECTOR')")
    @Auditable(action = SectorAction.DELETED)
    fun deleteSectorById(id: Int) {
        logger.info("Deleting Sector With Id $id")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
        logger.info("Sector with id $id deleted")
        AuditContext.deleted(delete.toAuditPayload())
    }
}
