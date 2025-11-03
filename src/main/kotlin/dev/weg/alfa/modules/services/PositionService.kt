package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.mappers.applyPatch
import dev.weg.alfa.modules.models.mappers.toEntity
import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.position.PositionRequest
import dev.weg.alfa.modules.models.position.PositionPatch
import dev.weg.alfa.modules.repositories.PositionRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PositionService(private val repository: PositionRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createPosition(request: PositionRequest): Position {
        logger.info("Creating position on floor ${request.floor}")
        val position = request.toEntity()
        return repository.save(position)
    }

    fun getAllPositions(): List<Position> {
        logger.info("Retrieving all positions from the database.")
        val position = repository.findAll()
        logger.info("Found ${position.size} Employee on the database.")
        return position
    }

    fun updatePosition(command: Pair<Int, PositionPatch>): Position {
        val (id, request) = command
        logger.info("Updating position with ID: $id")
        val oldPosition = repository.findByIdOrThrow(id)

        val newPosition = oldPosition.applyPatch(request)
        val updatePosition = repository.save(newPosition)

        return updatePosition
    }

    fun deletePositionById(id: Int) {
        logger.info("Deleting position with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
        logger.info("position with ID $id deleted with successfully.")
    }
}
