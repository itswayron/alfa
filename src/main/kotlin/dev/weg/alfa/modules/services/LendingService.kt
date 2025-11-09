package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.lending.LendingRequest
import dev.weg.alfa.modules.models.lending.LendingResponse
import dev.weg.alfa.modules.models.lending.toEntity
import dev.weg.alfa.modules.models.lending.toResponse
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.LendingRepository
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.simpleEntities.LendingStatusRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
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

    fun createLending(request: LendingRequest): LendingResponse {
        val status = repositoryStatus.findByIdOrThrow(1)
        val employee = repositoryEmployee.findByIdOrThrow(request.employeeId)
        val tool = repositoryTool.findByIdOrThrow(request.toolId)
        logger.info("creating a loan for the tool ${tool.name} by employee ${employee.name}")

        val lending = request.toEntity(status = status, tool = tool, employee = employee)

        val response = repositoryLending.save(lending).toResponse()
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
