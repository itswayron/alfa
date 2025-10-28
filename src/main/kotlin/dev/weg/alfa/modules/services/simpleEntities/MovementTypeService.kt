package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MovementType
import dev.weg.alfa.modules.repositories.simpleEntities.MovementTypeRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class MovementTypeService (private val repository: MovementTypeRepository){
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional()
    fun getAllMovementTypes(): List<MovementType>{
        logger.info("Retrieving all MovementTypes from the database.")
        val unities = repository.findAll()
        logger.info("Found ${unities.size} MovementTypes om the database")
        return unities
    }
}