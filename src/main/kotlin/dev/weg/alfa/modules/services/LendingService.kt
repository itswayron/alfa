package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.lending.LendingRequest
import dev.weg.alfa.modules.models.lending.LendingResponse
import dev.weg.alfa.modules.models.lending.ReturnLending
import dev.weg.alfa.modules.models.lending.returnWith
import dev.weg.alfa.modules.models.lending.toEntity
import dev.weg.alfa.modules.models.lending.toResponse
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.LendingRepository
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.simpleEntities.LendingStatusRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LendingService(
    private val repositoryLending: LendingRepository,
    private val repositoryStatus: LendingStatusRepository,
    private val repositoryEmployee: EmployeeRepository,
    private val repositoryTool: ToolRepository,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createLending(request: LendingRequest): LendingResponse {
        val status = repositoryStatus.findByIdOrThrow(1)
        val employee = repositoryEmployee.findByIdOrThrow(request.employeeId)
        val tool = repositoryTool.findByIdOrThrow(request.toolId)
        logger.info("Creating a loan for the tool ${tool.name} by employee ${employee.name}")

        if(tool.isLoan || tool.maximumUsages == tool.actualUsages) {
            // TODO: create a better way to validate toolUsage (i.e. a toolStatus variable)
            val messageError = "Tool ${tool.name} ID=${tool.id} is already loaned."
            logger.error(messageError)
            throw IllegalStateException(messageError)
            // TODO: Create custom exception for this case
        }

        tool.isLoan = true
        val lending = request.toEntity(
            status = status,
            employee = employee,
            tool = tool
        )
        repositoryTool.save(tool)

        val response = repositoryLending.save(lending).toResponse()
        return response
    }

    @Transactional
    fun returnTool(lendingId: Int, returnLending: ReturnLending): LendingResponse {
        val lending = repositoryLending.findByIdOrThrow(lendingId)
        val status = repositoryStatus.findByIdOrThrow(2)
        val tool = lending.tool
        logger.info("Returning lending ID=${lendingId} tool ID=${tool.id} at ${returnLending.timeOfReturn}")

        if(returnLending.timeOfReturn.isBefore(lending.departureTime)) {
            val errorMessage = "The time of return is before the departure time"
            logger.error(errorMessage)
            throw IllegalStateException(errorMessage)
            // TODO: Create custom exception for this case
        }

        if(!tool.isLoan) {
            val errorMessage = "Can't return an available tool"
            logger.error(errorMessage)
            throw IllegalStateException(errorMessage)
            // TODO: Create custom exception for this case
        }

        val updatedLending = lending.returnWith(returnLending, status)

        tool.actualUsages++
        tool.isLoan = false
        repositoryTool.save(tool)

        val response = repositoryLending.save(updatedLending).toResponse()
        return response
    }

    fun getAllLending(): List<LendingResponse> {
        logger.info("Retrieving all Lendings from the database.")
        val lendings = repositoryLending.findAll()
        val response = lendings.map { it.toResponse() }
        logger.info("Found ${response.size} Lendings on the database.")
        return response
    }

    fun deleteLendingById(id: Int) {
        logger.info("Deleting Lending with id: $id.")
        val delete = repositoryLending.findByIdOrThrow(id)
        repositoryLending.delete(delete)
        logger.info("Lending with ID $id deleted with successfully.")
    }
}
