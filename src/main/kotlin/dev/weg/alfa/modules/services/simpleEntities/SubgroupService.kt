
package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.SubgroupAction
import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.models.simpleModels.audit.toAuditPayload
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
    @Auditable(action = SubgroupAction.CREATED)
    fun createSubgroup(request: NameRequest): Subgroup {
        logger.info("Creating Subgroup with name: ${request.name}.")
        val response = repository.save(Subgroup(name = request.name))
        logger.info("Subgroup created with id: ${response.id}.")
        AuditContext.created(response.toAuditPayload())
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
    @Auditable(action = SubgroupAction.UPDATED)
    fun editSubgroup(command: Pair<Int, NameRequest>): Subgroup {
        val (id, newSubgroup) = command
        logger.info("Updating Subgroup with $id with name: ${newSubgroup.name}.")
        val oldSubgroup = repository.findByIdOrThrow(id)
        val updateSubgroup = repository.save(Subgroup(id = oldSubgroup.id, name = newSubgroup.name))
        logger.info("SubGroup name updated to ${newSubgroup.name}")
        AuditContext.updated(oldSubgroup.toAuditPayload(), updateSubgroup.toAuditPayload())
        return updateSubgroup
    }

    @PreAuthorize("hasAuthority('MANAGE_SUBGROUP')")
    @Auditable(action = SubgroupAction.DELETED)
    fun deleteSubGroupById(id: Int) {
        logger.info("Deleting SubGroup with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
        logger.info("SubGroup with id: $id deleted.")
        AuditContext.deleted(delete.toAuditPayload())
    }
}
