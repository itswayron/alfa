
package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SubgroupService(private val repository: SubgroupRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createSubgroup(request: NameRequest): Subgroup {
        logger.info("Creating Subgroup with name: ${request.name}.")
        val response = repository.save(Subgroup(name = request.name))
        return response
    }

    fun getAllSubgroups(): List<Subgroup> {
        logger.info("Retrieving all subgroups from the database.")
        val subgroup = repository.findAll()
        logger.info("Found ${subgroup.size} subgroups on the database.")
        return subgroup
    }

    fun editSubgroup(command: Pair<Int, NameRequest>): Subgroup {
        val (id, newSubgroup) = command
        logger.info("Updating Subgroup with $id with name: ${newSubgroup.name}.")
        val oldSubgroup = repository.findByIdOrThrow(id)
        val updateSubgroup = repository.save(Subgroup(id = oldSubgroup.id, name = newSubgroup.name))
        logger.info("SubGroup name updated to ${newSubgroup.name}")
        return updateSubgroup
    }

    fun deleteSubGroupById(id: Int) {
        logger.info("Deleting SubGroup with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
    }
}
