package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
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

@Service
class MovementBatchService(
    private val repository: MovementBatchRepository,
    private val partnerRepository: BusinessPartnerRepository,
    private val movementService: MovementService,
    private val movementRepository: MovementRepository,
    //private val validator: Validator<MovementBatch>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createMovementBatch(request: MovementBatchRequest): MovementBatchResponse {
        logger.info("Creating Movement Batch with code: ${request.code}")

        val mainSupplier = partnerRepository.findByIdIfNotNull(request.partnerId)

        val newOrder = request.toEntity(mainSupplier)

        // validator.validate(newOrder)
        val entity = repository.save(newOrder)
        logger.info("Movement Batch: ${entity.code} created with id: ${entity.id}")

        val movements: List<MovementResponse> = request.movementsList.map { originalReq ->
            val req = originalReq.copy(movementBatchId = entity.id)
            movementService.createMovement(req)
        }

        return entity.toResponse(movements)
    }

    fun getBatchByIdentifier(identifier: String): MovementBatchResponse {
        logger.info("Fetching Movement Batch with identifier: $identifier")
        val batch = repository.findByCode(identifier) ?: identifier.toIntOrNull()?.let { id ->
            repository.findByIdOrThrow(id)
        } ?: throw EntityNotFoundException("MovementBatch not found for '$identifier'")

        logger.info("Retrieved Movement Batch with ID: $identifier code: ${batch.code}")
        val movements = movementRepository.findAllByMovementBatchId(batch.id).map {
            it.toResponse()
        }

        return batch.toResponse(movements)
    }

    fun getBatches(
        code: String?,
        document: String?,
        text: String?,
        pageable: Pageable,
    ): PageDTO<MovementBatchResponse> {
        logger.info("Fetching batches from the repository.")

        val batches = repository.findFiltered(code, document, text, pageable)
        val total = repository.count()
        logger.debug("Found {} matching records out of total {} batches", batches.size, total)
        val pageDTO = PageDTO(
            content = batches.map { it.toResponse(emptyList()) },
            totalElements = total,
            totalPages = (total / pageable.pageSize).toInt() + 1,
            currentPage = pageable.pageNumber,
            pageSize = pageable.pageSize
        )
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

        // validator.validate(updatedOrder)
        val response = repository.save(updatedOrder).toResponse(emptyList())
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
