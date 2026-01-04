package dev.weg.alfa.modules.services

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.LendingAction
import dev.weg.alfa.modules.models.lending.*
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.models.tool.toAuditPayload
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.LendingRepository
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

// TODO: Unit Test : Should create lending successfully updating tool state and saving lending
// TODO: Unit Test : Should fail creation when tool is already loaned
// TODO: Unit Test : Should fail creation when tool reached maximum usages
// TODO: Unit Test : Should save tool before saving lending during creation

// TODO: Unit Test : Should return tool successfully updating usages and status
// TODO: Unit Test : Should fail return when returnTime is before departureTime
// TODO: Unit Test : Should fail return when tool is not marked as loaned
// TODO: Unit Test : Should save updated tool before saving updated lending on return

// TODO: Unit Test : Should retrieve all lendings and convert them to response DTOs

// TODO: Unit Test : Should delete lending by id calling repository methods correctly

// TODO: Integration Test : Should persist full lending lifecycle (create → return → fetch)
// TODO: Integration Test : Should fail creation for loaned or exhausted tool
// TODO: Integration Test : Should fail return with invalid timestamps or inconsistent tool state
// TODO: Integration Test : Should increment tool actual usages after return
// TODO: Integration Test : Should delete lending and reflect changes in database
@Service
class LendingService(
    private val lendingRepository: LendingRepository,
    private val employeeRepository: EmployeeRepository,
    private val toolRepository: ToolRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    @PreAuthorize("hasAuthority('CREATE_AND_RETURN_LENDING')")
    @Auditable(LendingAction.CREATED)
    fun createLending(request: LendingRequest): LendingResponse {
        val employee = employeeRepository.findByIdOrThrow(request.employeeId)
        val tool = toolRepository.findByIdOrThrow(request.toolId)
        logger.info("Creating a loan for the tool ${tool.name} by employee ${employee.name}")
        AuditContext.before(tool.toAuditPayload())

        val lending = request.toEntity(
            status = LendingStatus.PENDING,
            employee = employee,
            tool = tool
        )
        val loanedTool = tool.setLent()
        toolRepository.save(loanedTool)
        val saved = lendingRepository.save(lending)
        logger.info("Lending created for tool ${tool.name} by employee ${employee.name} estimated return at ${lending.estimatedReturn}")
        AuditContext.after(saved.toAuditPayload())
        return saved.toResponse()
    }

    @Transactional
    @PreAuthorize("hasAuthority('CREATE_AND_RETURN_LENDING')")
    @Auditable(action = LendingAction.RETURNED)
    fun returnTool(lendingId: Int, returnLending: ReturnLending): LendingResponse {
        val lending = lendingRepository.findByIdOrThrow(lendingId)
        val tool = lending.tool
        logger.info("Returning lending ID=${lendingId} tool ID=${tool.id} at ${returnLending.timeOfReturn}")
        AuditContext.before(tool.toAuditPayload())

        val updatedLending = lending.returnWith(returnLending, LendingStatus.RETURNED)
        val returnedTool = tool.unsetLent()

        toolRepository.save(returnedTool)
        val savedLending = lendingRepository.save(updatedLending)

        logger.info("Lending ID=${lendingId} returned for tool ID=${tool.id}")
        AuditContext.after(savedLending.toAuditPayload())
        val response = savedLending.toResponse()
        return response
    }

    @PreAuthorize("hasAuthority('VIEW_LENDING')")
    fun getAllLending(): List<LendingResponse> {
        logger.info("Retrieving all Lendings from the database.")
        val lendings = lendingRepository.findAll()
        val response = lendings.map { it.toResponse() }
        logger.info("Found ${response.size} Lendings on the database.")
        return response
    }

    @PreAuthorize("hasAuthority('MANAGE_LENDING')")
    @Transactional
    @Auditable(action = LendingAction.DELETED)
    fun deleteLendingById(id: Int) {
        logger.info("Deleting Lending with id: $id.")
        val delete = lendingRepository.findByIdOrThrow(id)
        val tool = delete.tool
        if (delete.timeOfReturn != null && tool.isLoan) {
            tool.isLoan = false
        }
        toolRepository.save(tool)
        lendingRepository.delete(delete)
        logger.info("Lending with ID $id deleted with successfully.")
        AuditContext.deleted(delete.toAuditPayload())
    }
}
