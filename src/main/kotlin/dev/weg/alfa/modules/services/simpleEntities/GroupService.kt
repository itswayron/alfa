package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Group
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
    fun createGroup(request: NameRequest): Group {
        logger.info("Creating area with name: ${request.name}.")
        return repository.save(Group(name = request.name))
    }

    @PreAuthorize("hasAuthority('VIEW_GROUP')")
    fun getAllGroups(): List<Group> {
        logger.info("Retrieving all areas from the database.")
        val areas = repository.findAll()
        logger.info("Found ${areas.size} areas on the database.")
        return areas
    }

    @PreAuthorize("hasAuthority('MANAGE_GROUP')")
    fun editGroup(command: Pair<Int, NameRequest>): Group {
        val (id, newGroup) = command
        logger.info("Updating area with $id with name: ${newGroup.name}.")
        val oldGroup = repository.findByIdOrThrow(id)
        val updatedGroup = repository.save(Group(id = oldGroup.id, name = newGroup.name))
        logger.info("Group name updated to ${newGroup.name}")
        return updatedGroup
    }

    @PreAuthorize("hasAuthority('MANAGE_GROUP')")
    fun deleteGroupById(id: Int) {
        logger.info("Deleting Group with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
    }
}
