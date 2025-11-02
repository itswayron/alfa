package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.mappers.toDTO
import dev.weg.alfa.modules.models.mappers.toEntity
import dev.weg.alfa.modules.models.mappers.toResponse
import dev.weg.alfa.modules.models.mappers.applyPatch
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderPatch
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderRequest
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderResponse
import dev.weg.alfa.modules.repositories.ProductionOrderRepository
import dev.weg.alfa.modules.repositories.businessPartner.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class ProductionOrderService(
    private val repository: ProductionOrderRepository,
    private val partnerRepository: BusinessPartnerRepository,
    //private val validator: Validator<ProductionOrder>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createProductionOrder(request: ProductionOrderRequest): ProductionOrderResponse {
        logger.info("Creating Production Order with code: ${request.code}")

        val mainSupplier = partnerRepository.findByIdOrThrow(request.mainSupplierId)

        val newOrder = request.toEntity(mainSupplier)

        // validator.validate(newOrder)
        val entity = repository.save(newOrder)
        logger.info("Production Order: ${entity.code} created with id: ${entity.id}")

        return entity.toResponse()
    }

    fun getProductionOrderById(id: Int): ProductionOrderResponse {
        logger.info("Fetching Production Order with ID: $id")

        val order = repository.findByIdOrThrow(id)
        logger.info("Retrieved Production Order with ID: $id code: ${order.code}")

        return order.toResponse()
    }

    fun getProductionOrders(pageable: Pageable): PageDTO<ProductionOrderResponse> {
        logger.info("Fetching Production Orders from the repository.")

        val page = repository.findAll(pageable)
        logger.info("Retrieved {} Production Orders.", page.content.size)

        return page.map { it.toResponse() }.toDTO()
    }

    fun updateProductionOrder(command: Pair<Int, ProductionOrderPatch>): ProductionOrderResponse {
        val (id, patch) = command
        logger.info("Updating Production Order ID={} with patch: {}", id, patch)

        val oldOrder = repository.findByIdOrThrow(id)
        logger.debug("Original Production Order data: {}", oldOrder)

        val newSupplier = partnerRepository.findByIdIfNotNull(patch.mainSupplierId)

        val updatedOrder = oldOrder.applyPatch(
            patch = patch, partner = newSupplier
        )

        // validator.validate(updatedOrder)
        val response = repository.save(updatedOrder).toResponse()
        logger.info("Production Order updated: ID={}, code='{}'", response.id, response.code)

        return response
    }

    fun deleteProductionOrder(id: Int) {
        logger.info("Deleting Production Order with ID='{}'", id)
        val deletedOrder = repository.findByIdOrThrow(id)
        logger.info("Order to delete found ID='{}', code='{}'", deletedOrder.id, deletedOrder.code)

        repository.deleteById(deletedOrder.id)
        logger.info("Production Order deleted successfully. ID='{}'", id)
    }

    private inline fun <reified T : Any> JpaRepository<T, Int>.findByIdIfNotNull(id: Int?): T? =
        id?.let { findByIdOrThrow(it) }
}
