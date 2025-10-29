package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.employee.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, Int>
