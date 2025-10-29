package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.employee.EmployeeDTO
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EmployeeService (private val employeeRepository: EmployeeRepository,
                       private val sectorRepository: SectorRepository){
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createEmployee(request: EmployeeDTO): Employee {
        logger.info("Creating Employee with name: ${request.name}. and sector with id ${request.sectorId}")
        val sector: Sector = sectorRepository.findByIdOrThrow(request.sectorId)
        val employee = Employee(name = request.name, sector = sector)
        return employeeRepository.save(employee)
    }

    fun getAllEmployee(): List<Employee> {
        logger.info("Retrieving all Employees from the database.")
        val employee = employeeRepository.findAll()
        logger.info("Found ${employee.size} Employee on the database.")
        return employee
    }

    fun editEmployee(command: Pair<Int, EmployeeDTO>): Employee {
        val (id, request) = command
        logger.info("Updating Employee with $id with name: ${request.name}.")
        val oldEmployee = employeeRepository.findByIdOrThrow(id)
        val newSector = sectorRepository.findByIdOrThrow((request.sectorId))
        val updatedEmployee = employeeRepository.save(Employee(id = oldEmployee.id, name = request.name, sector = newSector))
        logger.info("Sucessfully, Employee name updated to ${request.name}")
        return updatedEmployee
    }

    fun deleteEmployeeById(id: Int) {
        logger.info("Deleting Employee with id: $id.")
        val delete = employeeRepository.findByIdOrThrow(id)
        employeeRepository.delete(delete)
        logger.info("Employee with ID $id deleted with successfully.")
    }
}