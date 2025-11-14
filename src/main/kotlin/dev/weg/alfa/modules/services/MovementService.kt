package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.movement.*
import dev.weg.alfa.modules.models.stock.Stock
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.MovementBatchRepository
import dev.weg.alfa.modules.repositories.MovementRepository
import dev.weg.alfa.modules.repositories.StockRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MovementStatusRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MovementTypeRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import dev.weg.alfa.modules.repositories.utils.findByIdIfNotNull
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MovementService(
    private val repository: MovementRepository,
    private val stockRepository: StockRepository,
    private val movementBatchRepository: MovementBatchRepository,
    private val movementTypeRepository: MovementTypeRepository,
    private val employeeRepository: EmployeeRepository,
    private val movementStatusRepository: MovementStatusRepository,
    private val sectorRepository: SectorRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createMovement(request: MovementRequest): MovementResponse {
        logger.info("Creating Movement for stock ID=${request.stockId}")

        logger.debug("Fetching dependencies for Movement creation...")
        val movementBatch = movementBatchRepository.findByIdIfNotNull(request.movementBatchId)
        val stock = stockRepository.findByIdOrThrow(request.stockId)
        val type = movementTypeRepository.findByIdOrThrow(request.typeId)
        val employee = employeeRepository.findByIdOrThrow(request.employeeId)
        val status = movementStatusRepository.findByIdOrThrow(request.statusId)
        val sector = sectorRepository.findByIdOrThrow(request.sectorId)
        logger.trace("All dependencies successfully fetched for Movement creation")

        val newMovement = request.toEntity(
            stock = stock,
            movementBatch = movementBatch,
            type = type,
            employee = employee,
            status = status,
            sector = sector
        )
        logger.debug("Movement entity built from request: {}", newMovement)

        val saved = repository.save(newMovement)
        logger.info("Movement saved with ID=${saved.id} for stock '${stock.item.description}'")

        calculateStockAmount(saved)
        if (type.affectsAveragePrice) {
            calculateAveragePrice(saved)
        }

        logger.info("Movement created with ID=${saved.id} for stock '${stock.item.description}'")
        return saved.toResponse()
    }

    fun getMovementById(id: Int): MovementResponse {
        logger.info("Fetching Movement with ID=$id")
        val movement = repository.findByIdOrThrow(id)
        logger.debug("Movement retrieved: {}", movement)
        return movement.toResponse()
    }

    fun getAllMovements(
        stockId: Int?,
        text: String?,
        typeId: Int?,
        statusId: Int?,
        pageable: Pageable
    ): PageDTO<MovementResponse> {
        logger.info("Fetching all Movements")
        val movements = repository.findFiltered(
            stockId = stockId,
            text = text,
            typeId = typeId,
            statusId = statusId,
            pageable = pageable
        )
        val total = repository.count()
        logger.debug("Found {} matching records out of total {} movements", movements.size, total)
        val pageDTO = PageDTO(
            content = movements.map { it.toResponse() },
            totalElements = total,
            totalPages = (total / pageable.pageSize).toInt() + 1,
            currentPage = pageable.pageNumber,
            pageSize = pageable.pageSize
        )
        logger.info("Returning filtered Stock page with {} elements (page {}/{})",
            pageDTO.content.size, pageDTO.currentPage + 1, pageDTO.totalPages
        )
        return pageDTO
    }

    fun updateMovement(movementId: Int, patch: MovementPatch): MovementResponse {
        logger.info("Updating Movement ID=$movementId")
        logger.debug("Applying patch: {}", patch)

        val oldMovement = repository.findByIdOrThrow(movementId)
        val movementBatch = movementBatchRepository.findByIdIfNotNull(patch.movementBatchId)
        val status = patch.statusId?.let { movementStatusRepository.findByIdOrThrow(it) }

        val updatedMovement = oldMovement.applyPatch(
            patch = patch,
            movementBatch = movementBatch,
            status = status ?: oldMovement.status
        )

        val saved = repository.save(updatedMovement)
        logger.info("Movement updated successfully ID=${saved.id}")
        logger.trace("Updated Movement entity: {}", saved)

        return saved.toResponse()
    }

    fun deleteMovement(id: Int) {
        logger.info("Deleting Movement ID=$id")
        val movement = repository.findByIdOrThrow(id)
        repository.delete(movement)
        logger.info("Movement deleted successfully ID=$id")
    }

    private fun calculateAveragePrice(movement: Movement) {
        val stock = movement.stock
        logger.debug("Calculating new average price for stock '${stock.item.description}'")
        val lastEntries = getLastMovementsAffectingAveragePrice(stock)
        if (lastEntries.isEmpty()) {
            logger.warn("No recent movements found affecting average price for '${stock.item.description}'")
            return
        }

        val totalAmount = lastEntries.sumOf { it.quantity }
        val totalValue = lastEntries.sumOf { (it.price ?: 0.0) * it.quantity }
        val newAveragePrice = if (totalAmount > 0) {
            totalValue / totalAmount
        } else {
            stock.averagePrice
        }

        val updatedStock = stock.copy(averagePrice = newAveragePrice)
        stockRepository.save(updatedStock)
        logger.info("Updated average price for '${stock.item.description}' to $newAveragePrice")
    }

    private fun calculateStockAmount(movement: Movement) {
        val type = movement.type
        val stock = movement.stock
        logger.debug("Calculating new stock quantity for '${stock.item.description}'")

        val sign = type.quantitySign ?: if (movement.quantity >= 0) 1 else -1
        val newQuantity = stock.currentAmount + (movement.quantity * sign)

        if (newQuantity < 0) {
            logger.error("Attempted to create negative stock for '${stock.item.description}' (current=${stock.currentAmount}, movement=${movement.quantity})")
            throw IllegalStateException("Movimentação resultaria em estoque negativo para '${stock.item.description}'")
            // TODO: Implement a personalized exception for this case.
        }

        val updatedStock = stock.copy(currentAmount = newQuantity)
        stockRepository.save(updatedStock)
        logger.info("Stock quantity updated for '${stock.item.description}': $newQuantity")
    }

    private fun getLastMovementsAffectingAveragePrice(stock: Stock, limit: Int = 10): List<Movement> {
        val pageable = PageRequest.of(0, limit)
        logger.trace("Fetching last $limit movements affecting average price for stock '${stock.item.description}'")
        return repository.findLastMovementsAffectingAveragePrice(stock, pageable)
    }
}
