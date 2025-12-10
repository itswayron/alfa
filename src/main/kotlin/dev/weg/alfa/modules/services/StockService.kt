package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.stock.*
import dev.weg.alfa.modules.repositories.ItemRepository
import dev.weg.alfa.modules.repositories.PositionRepository
import dev.weg.alfa.modules.repositories.StockRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

// TODO: Unit Test : Should create stock using correct repositories and return proper DTO
// TODO: Unit Test : Should throw when itemId does not exist during creation
// TODO: Unit Test : Should throw when sectorId does not exist during creation
// TODO: Unit Test : Should throw when positionId does not exist during creation

// TODO: Unit Test : Should update stock applying only patched fields
// TODO: Unit Test : Should fetch sector only when sectorId is present in patch
// TODO: Unit Test : Should fetch position only when positionId is present in patch
// TODO: Unit Test : Should propagate exception when stockId does not exist during update

// TODO: Unit Test : Should delete stock calling repository delete with correct entity

// TODO: Integration Test : Should persist full stock lifecycle (create → fetch → delete)
// TODO: Integration Test : Should update stock partially and persist changes correctly
// TODO: Integration Test : Should filter stocks using specification
// TODO: Integration Test : Should paginate results correctly
// TODO: Integration Test : Should fail creation when FK references invalid entity
@Service
class StockService(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val sectorRepository: SectorRepository,
    private val positionRepository: PositionRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createStock(request: StockRequest): StockResponse {
        logger.info("Creating Stock for item ID=${request.itemId}")

        val item = itemRepository.findByIdOrThrow(request.itemId)
        val sector = sectorRepository.findByIdOrThrow(request.sectorId)
        val position = positionRepository.findByIdOrThrow(request.positionId)

        val newStock = request.toEntity(item = item, sector = sector, position = position)
        val savedStock = stockRepository.save(newStock)

        logger.info("Stock created with ID=${savedStock.id} for item '${item.description}'")
        return savedStock.toResponse()
    }

    fun getStockById(id: Int): StockResponse {
        logger.info("Fetching Stock with ID=$id")
        val stock = stockRepository.findByIdOrThrow(id)
        return stock.toResponse()
    }

    fun getStocks(pageable: Pageable): PageDTO<StockResponse> {
        logger.info("Fetching all Stocks, pageable=$pageable")
        val page = stockRepository.findAll(pageable)
        return page.map { it.toResponse() }.toDTO()
    }

    fun getFilteredStocks(
        filter: StockFilter,
        pageable: Pageable,
    ): PageDTO<StockResponse> {
        logger.info("Fetching filtered Stocks with filter={}, pageable={}", filter, pageable)
        logger.debug("Querying database with applied filters...")
        val spec = filter.toSpecification()
        val stocks = stockRepository.findAll(spec, pageable)

        logger.debug("Found {} matching records out of total {} stocks", stocks.numberOfElements, stockRepository.count())

        val pageDTO = stocks.map { it.toResponse() }.toDTO()
        logger.info(
            "Returning filtered Stock page with {} elements (page {}/{})",
            pageDTO.content.size, pageDTO.currentPage + 1, pageDTO.totalPages
        )
        return pageDTO
    }

    fun updateStock(stockId: Int, patch: StockPatch): StockResponse {
        logger.info("Patching Stock ID=$stockId with data: $patch")
        val oldStock = stockRepository.findByIdOrThrow(stockId)

        val sector = patch.sectorId?.let { sectorRepository.findByIdOrThrow(it) }
        val position = patch.positionId?.let { positionRepository.findByIdOrThrow(it) }

        val updatedStock = oldStock.applyPatch(patch, sector = sector, position = position)
        val savedStock = stockRepository.save(updatedStock)

        logger.info("Stock updated: ID=${savedStock.id}, item='${savedStock.item.description}'")
        return savedStock.toResponse()
    }

    fun deleteStock(stockId: Int) {
        logger.info("Deleting Stock with ID=$stockId")
        val stock = stockRepository.findByIdOrThrow(stockId)
        stockRepository.delete(stock)
        logger.info("Stock deleted successfully: ID=$stockId, item='${stock.item.description}'")
    }
}
