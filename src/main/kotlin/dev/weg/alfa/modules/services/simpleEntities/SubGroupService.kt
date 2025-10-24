
package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.simpleEntities.SubGroupRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SubgroupService(private val repository: SubGroupRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createSubgroup(request: NameRequest): Subgroup {
        logger.info("Creating Subgroup with name: ${request.name}.")
        return repository.save(Subgroup(name = request.name))
    }

    fun getAllSubgroups(): List<Subgroup> {
        logger.info("Retrieving all subgroup from the database.")
        val subgroup = repository.findAll()
        logger.info("Found ${subgroup.size} subgroup on the database.")
        return subgroup
    }

    fun editSubgroup(command: Pair<Int, NameRequest>): Subgroup {
        val (id, newSubGroup) = command
        logger.info("Updating Subgroup with $id with name: ${newSubGroup.name}.")
        val oldSubgroup = repository.findByIdOrThrow(id)
        val updateSubGroup = repository.save(Subgroup(id = oldSubgroup.id, name = newSubGroup.name))
        logger.info("SubGroup name updated to ${newSubGroup.name}")
        return updateSubGroup
    }

    fun deleteSubGroupById(id: Int) {
        logger.info("Deleting SubGroup with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
    }
}
