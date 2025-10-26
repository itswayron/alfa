package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.MovementStatus.*
import dev.weg.alfa.modules.models.MovementStatus.toEntity
import dev.weg.alfa.modules.models.MovementStatus.toResponse
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.simpleEntities.MovementStatausRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MovimentStatausService(private val repository: MovementStatausRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createMovimentStataus(request: MovementStatusRequest): MovementStatusResponse {
        logger.info("Creating MovimentStataus with status: ${request.status}.")
        return repository.save(request.toEntity()).toResponse()
    }

    @Transactional()
    fun getAllMovimentStataus(): List<MovementStatusResponse> {
        logger.info("Retrieving all MovimentStataus from the database.")
        val unities = repository.findAll()
        logger.info("Found ${unities.size} MovimentStataus on the database.")
        return unities.map { it.toResponse() }
    }

    @Transactional
    fun editMovimentStataus(id:Int, request: MovementStatusRequest): MovementStatusResponse {

        logger.info("Updating MovimentStataus with $id with status: ${request.status}.")
        val oldMovimentStataus = repository.findByIdOrThrow(id)
        val updatedMovimentStataus = repository.save(MovementStatus(id = oldMovimentStataus.id, name = request.status))
        logger.info("MovimentStataus name updated to ${request.status}")
        return updatedMovimentStataus.toResponse()
    }

    @Transactional
    fun deleteMovimentStatausById(id: Int) {
        logger.info("Deleting MovimentStataus with id: $id.")
        repository.findByIdOrThrow(id)
        repository.deleteById(id)
    }
}
