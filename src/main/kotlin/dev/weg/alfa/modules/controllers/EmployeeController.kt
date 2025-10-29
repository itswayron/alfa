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
class EmployeeController (private val service: EmployeeService){
    @PostMapping
    fun createEmployee(@RequestBody request: EmployeeDTO): ResponseEntity<Employee>{
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createEmployee(request))
        return response
    }

    @GetMapping
    fun getAllEmployee(): ResponseEntity<List<Employee>>{
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllEmployee())
        return response
    }

    @PatchMapping("/{id}")
    fun updateEmployee(@PathVariable id:Int, @RequestBody request: EmployeeDTO): ResponseEntity<Employee>{
        val response = ResponseEntity.status(HttpStatus.OK).body(service.editEmployee(Pair(id,request)))
        return response
    }

    @DeleteMapping("/{id}")
    fun deleteEmployee(@PathVariable id: Int): ResponseEntity<Employee> {
        service.deleteEmployeeById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}