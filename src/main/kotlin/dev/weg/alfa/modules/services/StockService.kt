package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.stock.*
import dev.weg.alfa.modules.repositories.PositionRepository
import dev.weg.alfa.modules.repositories.StockRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import dev.weg.alfa.modules.repositories.ItemRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

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
        text: String?,
        groupId: Int?,
        subgroupId: Int?,
        supplierId: Int?,
        isActive: Boolean?,
        pageable: Pageable,
    ): PageDTO<StockResponse> {
        logger.info("Fetching filtered Stocks with filters: text='{}', groupId={}, subgroupId={}, supplierId={}, pageable={}",
            text, groupId, subgroupId, supplierId, pageable
        )
        logger.debug("Querying database with applied filters...")
        val stocks = stockRepository.findFiltered(
            text = text,
            groupId = groupId,
            subgroupId = subgroupId,
            supplierId = supplierId,
            isActive = isActive,
            pageable = pageable,
        )

        val total = stockRepository.count()
        logger.debug("Found {} matching records out of total {} stocks", stocks.size, total)

        val pageDTO = PageDTO(
            content = stocks.map { it.toResponse() },
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
