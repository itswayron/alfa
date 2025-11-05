package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.mappers.applyPatch
import dev.weg.alfa.modules.models.mappers.toEntity
import dev.weg.alfa.modules.models.mappers.toResponse
import dev.weg.alfa.modules.models.movement.MovementPatch
import dev.weg.alfa.modules.models.movement.MovementRequest
import dev.weg.alfa.modules.models.movement.MovementResponse
import dev.weg.alfa.modules.repositories.*
import dev.weg.alfa.modules.repositories.simpleEntities.MovementStatusRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MovementTypeRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MovementService(
    private val repository: MovementRepository,
    private val stockRepository: StockRepository,
    private val productionOrderRepository: ProductionOrderRepository,
    private val movementTypeRepository: MovementTypeRepository,
    private val employeeRepository: EmployeeRepository,
    private val movementStatusRepository: MovementStatusRepository,
    private val sectorRepository: SectorRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createMovement(request: MovementRequest): MovementResponse {
        logger.info("Creating Movement for stock ID=${request.stockId}")

        val stock = stockRepository.findByIdOrThrow(request.stockId)
        val productionOrder = productionOrderRepository.findByIdIfNotNull(request.productionOrderId)
        val type = movementTypeRepository.findByIdOrThrow(request.typeId)
        val employee = employeeRepository.findByIdOrThrow(request.employeeId)
        val status = movementStatusRepository.findByIdOrThrow(request.statusId)
        val sector = sectorRepository.findByIdOrThrow(request.sectorId)

        val newMovement = request.toEntity(
            stock = stock,
            productionOrder = productionOrder,
            type = type,
            employee = employee,
            status = status,
            sector = sector
        )

        val saved = repository.save(newMovement)
        logger.info("Movement created with ID=${saved.id} for stock '${stock.item.description}'")

        return saved.toResponse()
    }

    fun getMovementById(id: Int): MovementResponse {
        logger.info("Fetching Movement with ID=$id")
        val movement = repository.findByIdOrThrow(id)
        return movement.toResponse()
    }

    fun getAllMovements(): List<MovementResponse> {
        logger.info("Fetching all Movements")
        val movements = repository.findAll()
        logger.info("Found ${movements.size} movements")
        return movements.map { it.toResponse() }
    }

    fun updateMovement(movementId: Int, patch: MovementPatch): MovementResponse {
        logger.info("Updating Movement ID=$movementId with patch: $patch")

        val oldMovement = repository.findByIdOrThrow(movementId)
        val productionOrder = productionOrderRepository.findByIdIfNotNull(patch.productionOrderId)
        val status = patch.statusId?.let { movementStatusRepository.findByIdOrThrow(it) }

        val updatedMovement = oldMovement.applyPatch(
            patch = patch,
            productionOrder = productionOrder,
            status = status ?: oldMovement.status
        )

        val saved = repository.save(updatedMovement)
        logger.info("Movement updated successfully ID=${saved.id}")

        return saved.toResponse()
    }

    fun deleteMovement(id: Int) {
        logger.info("Deleting Movement ID=$id")
        val movement = repository.findByIdOrThrow(id)
        repository.delete(movement)
        logger.info("Movement deleted successfully ID=$id")
    }
}
