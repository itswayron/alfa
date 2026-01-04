package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.GroupAction
import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.audit.toAuditPayload
import dev.weg.alfa.modules.repositories.simpleEntities.GroupRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class GroupService(private val repository: GroupRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_GROUP')")
    @Auditable(action = GroupAction.CREATED)
    fun createGroup(request: NameRequest): Group {
        logger.info("Creating group with name: ${request.name}.")
        val savedGroup = repository.save(Group(name = request.name))
        logger.info("Group created with id: ${savedGroup.id}.")
        AuditContext.created(savedGroup.toAuditPayload())
        return savedGroup
    }

    @PreAuthorize("hasAuthority('VIEW_GROUP')")
    fun getAllGroups(): List<Group> {
        logger.info("Retrieving all areas from the database.")
        val areas = repository.findAll()
        logger.info("Found ${areas.size} areas on the database.")
        return areas
    }

    @PreAuthorize("hasAuthority('MANAGE_GROUP')")
    @Auditable(action = GroupAction.UPDATED)
    fun editGroup(command: Pair<Int, NameRequest>): Group {
        val (id, newGroup) = command
        logger.info("Updating area with $id with name: ${newGroup.name}.")
        val oldGroup = repository.findByIdOrThrow(id)
        val updatedGroup = repository.save(Group(id = oldGroup.id, name = newGroup.name))
        logger.info("Group name updated to ${newGroup.name}")
        AuditContext.updated(oldGroup.toAuditPayload(), updatedGroup.toAuditPayload())
        return updatedGroup
    }

    @PreAuthorize("hasAuthority('MANAGE_GROUP')")
    @Auditable(action = GroupAction.DELETED)
    fun deleteGroupById(id: Int) {
        logger.info("Deleting Group with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
        AuditContext.deleted(delete.toAuditPayload())
    }
}
