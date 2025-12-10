package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
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

// TODO: Unit Test : Should create movement successfully saving entity and updating stock amount
// TODO: Unit Test : Should apply correct quantity sign logic and update new stock amount
// TODO: Unit Test : Should fail creation when resulting stock goes negative
// TODO: Unit Test : Should calculate average price only when movement type affects average price
// TODO: Unit Test : Should correctly calculate weighted average price from last movements
// TODO: Unit Test : Should ignore average price calculation when no movements found
// TODO: Unit Test : Should use default price=0.0 when movement price is null in average price calculation

// TODO: Unit Test : Should retrieve movement by id and map to response DTO
// TODO: Unit Test : Should filter and paginate movements returning valid PageDTO

// TODO: Unit Test : Should update movement applying only provided patch fields
// TODO: Unit Test : Should update movementBatch only when patch contains movementBatchId
// TODO: Unit Test : Should update movementStatus only when patch contains statusId

// TODO: Unit Test : Should delete movement by id and call repository delete

// TODO: Integration Test : Should persist full movement lifecycle updating stock and average price
// TODO: Integration Test : Should block movement creation when resulting stock becomes negative
// TODO: Integration Test : Should apply correct stock updates across multiple sequential movements
// TODO: Integration Test : Should calculate average price correctly in real database scenario
// TODO: Integration Test : Should update movement via patch and persist changes in database
// TODO: Integration Test : Should filter and paginate movements using real database queries
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
        filter: MovementFilter,
        pageable: Pageable
    ): PageDTO<MovementResponse> {
        logger.info("Fetching filtered Stocks with filter={}, pageable={}", filter, pageable)
        val specs = filter.toSpecification()
        val page = repository.findAll(specs, pageable)

        logger.debug("Found {} matching records out of total {} movements", page.numberOfElements, repository.count())
        val pageDTO = page.map { it.toResponse() }.toDTO()
        logger.info(
            "Returning filtered Stock page with {} elements (page {}/{})",
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
