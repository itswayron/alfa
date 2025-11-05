package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.StatusLending
import dev.weg.alfa.modules.repositories.StatusLendingRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class StatusLendingService(private val repository: StatusLendingRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllStatusLending(): List<StatusLending> {
        logger.info("Retrieving all Status Lending from the database.")
        val types = repository.findAll()
        logger.info("Found ${types.size} Status Lending on the database")
        return types
    }
}
