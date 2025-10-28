package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MovementStatus
import dev.weg.alfa.modules.repositories.simpleEntities.MovementStatausRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MovimentStatausService(private val repository: MovementStatausRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional()
    fun getAllMovimentStataus(): List<MovementStatus> {
        logger.info("Retrieving all MovimentStataus from the database.")
        val unities = repository.findAll()
        logger.info("Found ${unities.size} MovimentStataus on the database.")
        return unities
    }
}
