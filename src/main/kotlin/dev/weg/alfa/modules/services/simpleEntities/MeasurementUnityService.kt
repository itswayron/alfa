package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
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
    fun createMeasurementUnity(request: NameRequest): MeasurementUnity {
        logger.info("Creating MeasurementUnity with name: ${request.name}.")
        return repository.save(MeasurementUnity(name = request.name))
    }

    @PreAuthorize("hasAuthority('VIEW_MEASUREMENT_UNIT')")
    fun getAllMeasurementUnity(): List<MeasurementUnity> {
        logger.info("Retrieving all MeasurementUnity from the database.")
        val unities = repository.findAll()
        logger.info("Found ${unities.size} MeasurementUnity on the database.")
        return unities
    }

    @PreAuthorize("hasAuthority('MANAGE_MEASUREMENT_UNIT')")
    fun editMeasurementUnity(command: Pair<Int, NameRequest>): MeasurementUnity {
        val (id, newCustomer) = command
        logger.info("Updating MeasurementUnity with $id with name: ${newCustomer.name}.")
        val oldMeasurementUnity = repository.findByIdOrThrow(id)
        val updatedMeasurementUnity =
            repository.save(MeasurementUnity(id = oldMeasurementUnity.id, name = newCustomer.name))
        logger.info("MeasurementUnity name updated to ${newCustomer.name}")
        return updatedMeasurementUnity
    }

    @PreAuthorize("hasAuthority('MANAGE_MEASUREMENT_UNIT')")
    fun deleteMeasurementUnityById(id: Int) {
        logger.info("Deleting MeasurementUnity with id: $id.")
        repository.findByIdOrThrow(id)
        repository.deleteById(id)
    }
}
