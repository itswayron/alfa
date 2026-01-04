package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.UnitAction
import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.audit.toAuditPayload
import dev.weg.alfa.modules.repositories.simpleEntities.MeasurementUnityRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class MeasurementUnityService(private val repository: MeasurementUnityRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_MEASUREMENT_UNIT')")
    @Auditable(action = UnitAction.CREATED)
    fun createMeasurementUnity(request: NameRequest): MeasurementUnity {
        logger.info("Creating MeasurementUnity with name: ${request.name}.")
        val saved = repository.save(MeasurementUnity(name = request.name))
        logger.info("MeasurementUnity created with id: ${saved.id}.")
        AuditContext.created(saved.toAuditPayload())
        return saved
    }

    @PreAuthorize("hasAuthority('VIEW_MEASUREMENT_UNIT')")
    fun getAllMeasurementUnity(): List<MeasurementUnity> {
        logger.info("Retrieving all MeasurementUnity from the database.")
        val unities = repository.findAll()
        logger.info("Found ${unities.size} MeasurementUnity on the database.")
        return unities
    }

    @PreAuthorize("hasAuthority('MANAGE_MEASUREMENT_UNIT')")
    @Auditable(action = UnitAction.UPDATED)
    fun editMeasurementUnity(command: Pair<Int, NameRequest>): MeasurementUnity {
        val (id, newCustomer) = command
        logger.info("Updating MeasurementUnity with $id with name: ${newCustomer.name}.")
        val oldMeasurementUnity = repository.findByIdOrThrow(id)
        val updatedMeasurementUnity =
            repository.save(MeasurementUnity(id = oldMeasurementUnity.id, name = newCustomer.name))
        logger.info("MeasurementUnity name updated to ${newCustomer.name}")
        AuditContext.updated(oldMeasurementUnity.toAuditPayload(), updatedMeasurementUnity.toAuditPayload())
        return updatedMeasurementUnity
    }

    @PreAuthorize("hasAuthority('MANAGE_MEASUREMENT_UNIT')")
    @Auditable(action = UnitAction.DELETED)
    fun deleteMeasurementUnityById(id: Int) {
        logger.info("Deleting MeasurementUnity with id: $id.")
        val deleted = repository.findByIdOrThrow(id)
        repository.deleteById(id)
        logger.info("MeasurementUnity with id: $id deleted.")
        AuditContext.deleted(deleted.toAuditPayload())
    }
}
