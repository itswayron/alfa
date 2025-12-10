package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MovementStatus
import dev.weg.alfa.modules.repositories.simpleEntities.MovementStatusRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class MovementStatusService(private val repository: MovementStatusRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllMovementStatus(): List<MovementStatus> {
        logger.info("Retrieving all movement status from the database.")
        val status = repository.findAll()
        logger.info("Found ${status.size} movement status on the database.")
        return status
    }
}
