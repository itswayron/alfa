
package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class SubgroupService(private val repository: SubgroupRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_SUBGROUP')")
    fun createSubgroup(request: NameRequest): Subgroup {
        logger.info("Creating Subgroup with name: ${request.name}.")
        val response = repository.save(Subgroup(name = request.name))
        return response
    }

    @PreAuthorize("hasAuthority('VIEW_SUBGROUP')")
    fun getAllSubgroups(): List<Subgroup> {
        logger.info("Retrieving all subgroup from the database.")
        val subgroup = repository.findAll()
        logger.info("Found ${subgroup.size} subgroup on the database.")
        return subgroup
    }

    @PreAuthorize("hasAuthority('MANAGE_SUBGROUP')")
    fun editSubgroup(command: Pair<Int, NameRequest>): Subgroup {
        val (id, newSubgroup) = command
        logger.info("Updating Subgroup with $id with name: ${newSubgroup.name}.")
        val oldSubgroup = repository.findByIdOrThrow(id)
        val updateSubgroup = repository.save(Subgroup(id = oldSubgroup.id, name = newSubgroup.name))
        logger.info("SubGroup name updated to ${newSubgroup.name}")
        return updateSubgroup
    }

    @PreAuthorize("hasAuthority('MANAGE_SUBGROUP')")
    fun deleteSubGroupById(id: Int) {
        logger.info("Deleting SubGroup with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
    }
}
