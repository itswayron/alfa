package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.movement.MovementResponse
import dev.weg.alfa.modules.models.movement.toResponse
import dev.weg.alfa.modules.models.movementBatch.*
import dev.weg.alfa.modules.repositories.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.MovementBatchRepository
import dev.weg.alfa.modules.repositories.MovementRepository
import dev.weg.alfa.modules.repositories.utils.findByIdIfNotNull
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

// TODO: Unit Test : Should create a movement batch with supplier and call movementService for each movement
// TODO: Unit Test : Should create a movement batch without supplier (null partner) and still persist entity
// TODO: Unit Test : Should propagate exception thrown by movementService during creation
// TODO: Integration Test : Should persist batch and its movements with transactional consistency
// TODO: Integration Test : Should rollback entire operation if a movement creation fails

// TODO: Unit Test : Should fetch batch by code and return its movements
// TODO: Unit Test : Should fetch batch by numeric identifier (ID) when code is not found
// TODO: Unit Test : Should throw EntityNotFoundException when identifier is invalid
// TODO: Integration Test : Should fetch batch by code in real database
// TODO: Integration Test : Should fetch batch by ID in real database

// TODO: Unit Test : Should list batches using specification filter
// TODO: Unit Test : Should map movement count for each batch in paginated result
// TODO: Unit Test : Should return empty page when no batch matches filter
// TODO: Integration Test : Should correctly filter and paginate batches

// TODO: Unit Test : Should update supplier when patch contains supplierId
// TODO: Unit Test : Should not change supplier when patch has null supplierId
// TODO: Unit Test : Should apply patch fields correctly using applyPatch
// TODO: Unit Test : Should throw when batch does not exist during update
// TODO: Integration Test : Should update batch fields and persist changes

// TODO: Unit Test : Should delete existing movement batch
// TODO: Unit Test : Should throw EntityNotFoundException when deleting non-existing batch
// TODO: Integration Test : Should delete batch and evaluate cascade behavior on movements
@Service
class MovementBatchService(
    private val repository: MovementBatchRepository,
    private val partnerRepository: BusinessPartnerRepository,
    private val movementService: MovementService,
    private val movementRepository: MovementRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createMovementBatch(request: MovementBatchRequest): MovementBatchResponseWithList {
        logger.info("Creating Movement Batch with code: ${request.code}")

        val mainSupplier = partnerRepository.findByIdIfNotNull(request.partnerId)
        val newOrder = request.toEntity(mainSupplier)

        val entity = repository.save(newOrder)
        logger.info("Movement Batch: ${entity.code} created with id: ${entity.id}")

        val movements: List<MovementResponse> = request.movementsList.map { originalReq ->
            val req = originalReq.copy(movementBatchId = entity.id)
            movementService.createMovement(req)
        }

        return entity.toResponseWithList(movements)
    }

    fun getBatchByIdentifier(identifier: String): MovementBatchResponseWithList {
        logger.info("Fetching Movement Batch with identifier: $identifier")
        val batch = repository.findByCode(identifier) ?: identifier.toIntOrNull()?.let { id ->
            repository.findByIdOrThrow(id)
        } ?: throw EntityNotFoundException("MovementBatch not found for '$identifier'")

        logger.info("Retrieved Movement Batch with ID: $identifier code: ${batch.code}")
        val movements = movementRepository.findAllByMovementBatchId(batch.id).map {
            it.toResponse()
            // Possible N+1 problem
        }

        return batch.toResponseWithList(movements)
    }

    fun getBatches(
        filter: MovementBatchFilter,
        pageable: Pageable,
    ): PageDTO<MovementBatchResponse> {
        logger.info("Fetching filtered Batches with filters={}, pageable={}", filter, pageable)
        val specs = filter.toSpecification()
        val batches = repository.findAll(specs, pageable)

        logger.debug(
            "Found {} matching records out of total {} batches",
            batches.numberOfElements,
            repository.count()
        )

        val pageDTO = batches.map {
            it.toResponse(movementRepository.countByMovementBatchId(it.id))
        }.toDTO()
        logger.info(
            "Returning filtered batches page with {} elements (page {}/{})",
            pageDTO.content.size, pageDTO.currentPage + 1, pageDTO.totalPages
        )
        return pageDTO
    }

    fun updateMovementBatch(command: Pair<Int, MovementBatchPatch>): MovementBatchResponse {
        val (id, patch) = command
        logger.info("Updating Movement Batch ID={} with patch: {}", id, patch)

        val oldOrder = repository.findByIdOrThrow(id)
        logger.debug("Original Movement Batch data: {}", oldOrder)

        val newSupplier = partnerRepository.findByIdIfNotNull(patch.mainSupplierId)

        val updatedOrder = oldOrder.applyPatch(
            patch = patch,
            partner = newSupplier
        )

        val response = repository.save(updatedOrder).toResponse(movementRepository.countByMovementBatchId(id))
        logger.info("Movement Batch updated: ID={}, code='{}'", response.id, response.code)

        return response
    }

    fun deleteMovementBatchById(id: Int) {
        logger.info("Deleting Movement Batch with ID='{}'", id)
        val deletedOrder = repository.findByIdOrThrow(id)
        logger.info("Order to delete found ID='{}', code='{}'", deletedOrder.id, deletedOrder.code)

        repository.deleteById(deletedOrder.id)
        logger.info("Movement Batch deleted successfully. ID='{}'", id)
    }
}
