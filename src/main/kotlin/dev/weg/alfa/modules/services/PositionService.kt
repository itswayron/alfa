package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.position.PositionCreationRequest
import dev.weg.alfa.modules.models.position.PositionUpdateRequest
import dev.weg.alfa.modules.repositories.PositionRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PositionService(private val repository: PositionRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createPosition(request: PositionCreationRequest): Position {
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

    fun updatePositions(Command: Pair<Int, PositionUpdateRequest>): Position {
        val (id, request) = Command
        logger.info("Updating position with ID: $id")

        val oldPosition = repository.findByIdOrThrow(id)

        val newPosition = Position(
            id = oldPosition.id,
            floor = request.floor ?: oldPosition.floor,
            side = request.side ?: oldPosition.side,
            box = request.box ?: oldPosition.box,
            column = request.column ?: oldPosition.column,
        )
        val updatePosition = repository.save(newPosition)
        return updatePosition
    }

    fun deletePositionById(id: Int) {
        logger.info("Deleting position with id: $id.")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
        logger.info("position with ID $id deleted with successfully.")
    }

    fun getPositionByFloor(floor: String): List<Position> {
        logger.info("Retrieving positions by floor: $floor.")
        val positions = repository.findByFloorField(floor)
        logger.info("Found ${positions.size} positions on floor $floor.")
        return positions
    }
    fun getPositionBySide(side: String): List<Position> {
        logger.info("Retrieving positions by side: $side.")
        val positions = repository.findBySideField(side)
        logger.info("Found ${positions.size} positions on side $side.")
        return positions
    }
    fun getPositionByColumn(column: String): List<Position> {
        logger.info("Retrieving positions by column: $column.")
        val positions = repository.findByColumnField(column)
        logger.info("Found ${positions.size} positions on column $column.")
        return positions
    }
    fun getPositionByBox(box: String): List<Position> {
        logger.info("Retrieving positions by box: $box.")
        val positions = repository.findByBoxField(box)
        logger.info("Found ${positions.size} positions on box $box.")
        return positions
    }
}
