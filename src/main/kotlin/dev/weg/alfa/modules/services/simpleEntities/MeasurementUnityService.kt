package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.repositories.simpleEntities.MeasurementUnityRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class MeasurementUnityService(private val repository: MeasurementUnityRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun findMeasurementUnityByIdOrThrow(id: Int): MeasurementUnity = repository.findById(id).orElseThrow {
        logger.error("MeasurementUnity with id: $id does not exits.")
        throw EntityNotFoundException("MeasurementUnity with id: $id does not exits.")
    }

    fun createMeasurementUnity(request: NameRequest): MeasurementUnity {
        logger.info("Creating MeasurementUnity with name: ${request.name}.")
        return repository.save(MeasurementUnity(id = null, name = request.name))
    }

    fun getAllMeasurementUnity(): List<MeasurementUnity> {
        logger.info("Retrieving all MeasurementUnity from the database.")
        val unities = repository.findAll()
        logger.info("Found ${unities.size} MeasurementUnity on the database.")
        return unities
    }

    fun editMeasurementUnity(command: Pair<Int, NameRequest>): MeasurementUnity {
        val (id, newCustomer) = command
        logger.info("Updating MeasurementUnity with $id with name: ${newCustomer.name}.")
        val oldMeasurementUnity = findMeasurementUnityByIdOrThrow(id)
        val updatedMeasurementUnity = repository.save(MeasurementUnity(id = oldMeasurementUnity.id, name = newCustomer.name))
        logger.info("MeasurementUnity name updated to ${newCustomer.name}")
        return updatedMeasurementUnity
    }

    fun deleteMeasurementUnityById(id: Int) {
        logger.info("Deleting MeasurementUnity with id: $id.")
        findMeasurementUnityByIdOrThrow(id)
        repository.deleteById(id)
    }
}