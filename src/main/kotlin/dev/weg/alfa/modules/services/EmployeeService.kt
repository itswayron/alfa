package dev.weg.alfa.modules.services

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.EmployeeAction
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.employee.EmployeeRequest
import dev.weg.alfa.modules.models.employee.toAuditPayload
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val sectorRepository: SectorRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @Auditable(action = EmployeeAction.CREATED)
    fun createEmployee(request: EmployeeRequest): Employee {
        logger.info("Creating Employee with name: ${request.name} and sector with id ${request.sectorId}.")
        val sector = sectorRepository.findByIdOrThrow(request.sectorId)
        val employee = Employee(name = request.name, sector = sector)
        val response = employeeRepository.save(employee)
        logger.info("Employee created with name: ${response.name} sector=${employee.sector.name}")
        AuditContext.created(response.toAuditPayload())
        return response
    }

    @PreAuthorize("hasAuthority('READ_EMPLOYEE')")
    fun getAllEmployee(): List<Employee> {
        logger.info("Retrieving all employees from the database.")
        val response = employeeRepository.findAll()
        logger.info("Found ${response.size} employees on the database.")
        return response
    }

    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE')")
    @Auditable(action = EmployeeAction.UPDATED)
    fun editEmployee(command: Pair<Int, EmployeeRequest>): Employee {
        val (id, request) = command
        logger.info("Updating Employee with $id with name: ${request.name}.")
        val oldEmployee = employeeRepository.findByIdOrThrow(id)
        val newSector = sectorRepository.findByIdOrThrow(request.sectorId)
        val updatedEmployee = employeeRepository.save(
            Employee(
                id = oldEmployee.id,
                name = request.name,
                sector = newSector
            )
        )
        logger.info("Successfully, Employee name updated to ${request.name}")
        AuditContext.updated(oldEmployee.toAuditPayload(), updatedEmployee.toAuditPayload())
        return updatedEmployee
    }

    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE')")
    @Auditable(action = EmployeeAction.DELETED)
    fun deleteEmployeeById(id: Int) {
        logger.info("Deleting Employee with id: $id.")
        val delete = employeeRepository.findByIdOrThrow(id)
        employeeRepository.delete(delete)
        logger.info("Employee with ID $id deleted with successfully.")
        AuditContext.deleted(delete.toAuditPayload())
    }
}
