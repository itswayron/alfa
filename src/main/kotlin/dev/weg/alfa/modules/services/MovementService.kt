package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.movement.*
import dev.weg.alfa.modules.models.stock.Stock
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.MovementBatchRepository
import dev.weg.alfa.modules.repositories.MovementRepository
import dev.weg.alfa.modules.repositories.StockRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import dev.weg.alfa.modules.repositories.utils.findByIdIfNotNull
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
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
    private val employeeRepository: EmployeeRepository,
    private val sectorRepository: SectorRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    @PreAuthorize("hasAuthority('CREATE_MOVEMENT')")
    fun createMovement(request: MovementRequest): MovementResponse {
        logger.info("Creating Movement for stock ID=${request.stockId}")

        logger.debug("Fetching dependencies for Movement creation...")
        val movementBatch = movementBatchRepository.findByIdIfNotNull(request.movementBatchId)
        val stock = stockRepository.findByIdOrThrow(request.stockId)
        val employee = employeeRepository.findByIdOrThrow(request.employeeId)
        val sector = sectorRepository.findByIdOrThrow(request.sectorId)
        logger.trace("All dependencies successfully fetched for Movement creation")

        val newMovement = request.toEntity(
            stock = stock,
            movementBatch = movementBatch,
            employee = employee,
            sector = sector
        )
        logger.debug("Movement entity built from request: {}", newMovement)

        val lastAffectingMovements = repository.findLastMovementsFromStockType(
            stockId = stock.id,
            types = MovementType.getAffectingAveragePrice().toSet(),
        )

        val newCalculatedStock = stock.applyMovement(newMovement, lastAffectingMovements)
        val saved = repository.save(newMovement)
        stockRepository.save(newCalculatedStock)

        logger.info("Movement created with ID=${saved.id} for stock '${stock.item.description}'")
        return saved.toResponse()
    }

    @PreAuthorize("hasAuthority('VIEW_MOVEMENT')")
    fun getMovementById(id: Int): MovementResponse {
        logger.info("Fetching Movement with ID=$id")
        val movement = repository.findByIdOrThrow(id)
        logger.debug("Movement retrieved: {}", movement)
        return movement.toResponse()
    }

    @PreAuthorize("hasAuthority('VIEW_MOVEMENT')")
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

    @PreAuthorize("hasAuthority('UPDATE_MOVEMENT')")
    fun updateMovement(movementId: Int, patch: MovementPatch): MovementResponse {
        logger.info("Updating Movement ID=$movementId")
        logger.debug("Applying patch: {}", patch)

        val oldMovement = repository.findByIdOrThrow(movementId)
        val movementBatch = movementBatchRepository.findByIdIfNotNull(patch.movementBatchId)

        val updatedMovement = oldMovement.applyPatch(
            patch = patch,
            movementBatch = movementBatch,
        )

        val saved = repository.save(updatedMovement)
        recalculateStock(updatedMovement.stock)
        logger.info("Movement updated successfully ID=${saved.id}")
        logger.trace("Updated Movement entity: {}", saved)

        return saved.toResponse()
    }

    @PreAuthorize("hasAuthority('DELETE_MOVEMENT')")
    fun deleteMovement(id: Int) {
        logger.info("Deleting Movement ID=$id")
        val movement = repository.findByIdOrThrow(id)
        repository.delete(movement)
        recalculateStock(movement.stock)
        logger.info("Movement deleted successfully ID=$id")
    }

    @Transactional
    private fun recalculateStock(stock: Stock) {
        val movements = repository.findAllByStockIdOrderByDateAsc(stock.id)

        var recalculatedStock = stock.copy(currentAmount = 0.0, averagePrice = 0.0)

        movements.forEach { m ->
            val lastAffecting = movements.filter { it.date <= m.date && it.type.affectsAveragePrice }
            recalculatedStock = recalculatedStock.applyMovement(m, lastAffecting)
        }

        stockRepository.save(recalculatedStock)
    }
}
