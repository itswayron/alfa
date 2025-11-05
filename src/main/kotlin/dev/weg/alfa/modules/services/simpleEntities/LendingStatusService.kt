package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.repositories.LendingStatusRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class LendingStatusService(private val repository: LendingStatusRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllStatusLending(): List<LendingStatus> {
        logger.info("Retrieving all Status Lending from the database.")
        val statuses = repository.findAll()
        logger.info("Found ${statuses.size} Status Lending on the database")
        return statuses
    }
}
