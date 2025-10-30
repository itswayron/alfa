package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.employee.EmployeeDTO
import dev.weg.alfa.modules.services.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.EMPLOYEE)
class EmployeeController(private val service: EmployeeService) {
    @PostMapping
    fun createEmployee(@RequestBody request: EmployeeDTO): ResponseEntity<Employee> {
        val response = service.createEmployee(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllEmployee(): ResponseEntity<List<Employee>> {
        val response = service.getAllEmployee()
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{id}")
    fun updateEmployee(
        @PathVariable id: Int,
        @RequestBody request: EmployeeDTO
    ): ResponseEntity<Employee> {
        val response = service.editEmployee(Pair(id, request))
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteEmployee(@PathVariable id: Int): ResponseEntity<Employee> {
        service.deleteEmployeeById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
