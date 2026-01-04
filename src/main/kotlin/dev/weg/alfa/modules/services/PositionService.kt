package dev.weg.alfa.modules.services

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.PositionAction
import dev.weg.alfa.modules.models.position.*
import dev.weg.alfa.modules.repositories.PositionRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class PositionService(private val repository: PositionRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_POSITION')")
    @Auditable(action = PositionAction.CREATED)
    fun createPosition(request: PositionRequest): Position {
        logger.info("Creating position on floor ${request.floor}")
        val position = request.toEntity()
        logger.info("Position created=$position")
        val saved = repository.save(position)
        AuditContext.created(saved.toAuditPayload())
        return saved
    }

    @PreAuthorize("hasAuthority('VIEW_POSITION')")
    fun getAllPositions(): List<Position> {
        logger.info("Retrieving all positions from the database.")
        val position = repository.findAll()
        logger.info("Found ${position.size} Employee on the database.")
        return position
    }

    @PreAuthorize("hasAuthority('MANAGE_POSITION')")
    @Auditable(action = PositionAction.UPDATED)
    fun updatePosition(command: Pair<Int, PositionPatch>): Position {
        val (id, request) = command
        logger.info("Updating position with ID: $id")
        val oldPosition = repository.findByIdOrThrow(id)

        val newPosition = oldPosition.applyPatch(request)
        val updatePosition = repository.save(newPosition)
        logger.info("Position ID='$id' updated.")
        AuditContext.updated(oldPosition.toAuditPayload(), updatePosition.toAuditPayload())
        return updatePosition
    }

    @PreAuthorize("hasAuthority('MANAGE_POSITION')")
    @Auditable(action = PositionAction.DELETED)
    fun deletePositionById(id: Int) {
        logger.info("Deleting position with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
        logger.info("position with ID $id deleted with successfully.")
        AuditContext.deleted(delete.toAuditPayload())
    }
}
