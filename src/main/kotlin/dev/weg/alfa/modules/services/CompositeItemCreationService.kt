package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.compositeItem.CompositeItemRequest
import dev.weg.alfa.modules.models.stock.StockRequest
import dev.weg.alfa.modules.models.stock.StockResponse
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// TODO: Integration Test : Should create composite item with associated stock and position
// TODO: Integration Test : Should rollback all creations if any creation fails
// TODO: Integration Test : Create all entities and verify their relationships are correctly established
@Service
class CompositeItemCreationService(
    private val itemService: ItemService,
    private val positionService: PositionService,
    private val stockService: StockService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    @PreAuthorize("hasAuthority('COMPOSITE_ITEM_CREATION')")
    fun execute(request: CompositeItemRequest): StockResponse {
        logger.info("Creating Composite Item=({}) with associated Stock.", request.item)
        logger.debug("Full request: {}", request)

        logger.debug("Creating item...")
        val item = itemService.createItem(request.item)
        logger.debug("Creating position...")
        val position = positionService.createPosition(request.position)
        logger.debug("Creating stock...")

        val stockRequest = StockRequest(
            itemId = item.id,
            positionId = position.id,
            minimumAmount = request.stock.minimumAmount,
            maximumAmount = request.stock.maximumAmount,
            sectorId = request.stock.sectorId,
        )
        val stock = stockService.createStock(stockRequest)
        logger.debug("All components created successfully.")
        logger.info("Composite Item created with Stock ID=${stock.id} for Item ID=${item.id}")
        return stock
    }
}
