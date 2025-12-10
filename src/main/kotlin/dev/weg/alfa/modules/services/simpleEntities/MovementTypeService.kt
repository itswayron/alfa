package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MovementType
import dev.weg.alfa.modules.repositories.simpleEntities.MovementTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

// Tests: When and if this class grows in behavior, create unit tests.
@Service
class MovementTypeService (private val repository: MovementTypeRepository){
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllMovementTypes(): List<MovementType> {
        logger.info("Retrieving all Movement Types from the database.")
        val types = repository.findAll()
        logger.info("Found ${types.size} Movement Types on the database")
        return types
    }
}
