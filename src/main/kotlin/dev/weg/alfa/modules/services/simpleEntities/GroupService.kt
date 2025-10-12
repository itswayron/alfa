
package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.repositories.simpleEntities.GroupRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GroupService(private val repository: GroupRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun findGroupByIdOrThrow(id: Int): Group = repository.findById(id).orElseThrow {
        logger.error("Group with id: $id does not exits.")
        throw EntityNotFoundException("Group with id: $id does not exits.")
    }

    fun createGroup(request: NameRequest): Group {
        logger.info("Creating area with name: ${request.name}.")
        return repository.save(Group(id = 0, name = request.name))
    }

    fun getAllGroups(): List<Group> {
        logger.info("Retrieving all areas from the database.")
        val areas = repository.findAll()
        logger.info("Found ${areas.size} areas on the database.")
        return areas
    }

    fun editGroup(command: Pair<Int, NameRequest>): Group {
        val (id, newGroup) = command
        logger.info("Updating area with $id with name: ${newGroup.name}.")
        val oldGroup = findGroupByIdOrThrow(id)
        val updatedGroup = repository.save(Group(id = oldGroup.id, name = newGroup.name))
        logger.info("Group name updated to ${newGroup.name}")
        return updatedGroup
    }

    fun deleteGroupById(id: Int) {
        logger.info("Deleting Group with id: $id.")
        findGroupByIdOrThrow(id)
        repository.deleteById(id)
    }
}