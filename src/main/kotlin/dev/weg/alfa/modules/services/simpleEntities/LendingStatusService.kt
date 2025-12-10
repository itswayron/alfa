package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.repositories.simpleEntities.LendingStatusRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class LendingStatusService(private val repository: LendingStatusRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllStatusLending(): List<LendingStatus> {
        logger.info("Retrieving all Lending statuses from the database.")
        val statuses = repository.findAll()
        logger.info("Found ${statuses.size} Lending statuses on the database")
        return statuses
    }
}
