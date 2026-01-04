package dev.weg.alfa.modules.services

import BaseTest
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.employee.EmployeeRequest
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.repositories.EmployeeRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import dev.weg.alfa.modules.repositories.utils.FakeJpaRepository
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class EmployeeServiceTest : BaseTest() {

    private class FakeEmployeeRepository(
        findByIdAnswer: (Int) -> Optional<Employee>,
        private val saveAnswer: (Employee) -> Employee = { it },
        private val findAllAnswer: () -> List<Employee> = { emptyList() },
        private val deleteAnswer: (Employee) -> Unit = {}
    ) : FakeJpaRepository<Employee, Int>(findByIdAnswer), EmployeeRepository {

        override fun <S : Employee> save(entity: S): S {
            @Suppress("UNCHECKED_CAST")
            return saveAnswer(entity) as S
        }

        override fun findAll(): MutableList<Employee> = findAllAnswer().toMutableList()

        override fun delete(entity: Employee) {
            deleteAnswer(entity)
        }
    }

    private class FakeSectorRepository(
        findByIdAnswer: (Int) -> Optional<Sector>
    ) : FakeJpaRepository<Sector, Int>(findByIdAnswer), SectorRepository

    @AfterEach
    fun clearAuditContext() {
        AuditContext.consume()
    }

    @Test
    fun `createEmployee should create employee when sector exists`() {
        val sectorId = 10
        val sector = Sector(id = sectorId, name = "Sector A")

        val sectorRepo = spyk(
            FakeSectorRepository { id ->
                if (id == sectorId) Optional.of(sector) else Optional.empty()
            }
        )

        var savedArg: Employee? = null
        val employeeRepo = spyk(
            FakeEmployeeRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { e ->
                    savedArg = e
                    e.copy(id = 123)
                }
            )
        )

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        val request = EmployeeRequest(name = "Ana", sectorId = sectorId)
        val result = service.createEmployee(request)

        assertEquals(123, result.id)
        assertEquals("Ana", result.name)
        assertSame(sector, result.sector)

        assertEquals("Ana", savedArg!!.name)
        assertSame(sector, savedArg!!.sector)

        verify(exactly = 1) { sectorRepo.findById(sectorId) }
        verify(exactly = 1) { employeeRepo.save(any<Employee>()) }
    }

    @Test
    fun `createEmployee should throw when sector does not exist on create`() {
        val sectorRepo = spyk(FakeSectorRepository { Optional.empty() })
        val employeeRepo = spyk(FakeEmployeeRepository(findByIdAnswer = { Optional.empty() }))

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        val request = EmployeeRequest(name = "Ana", sectorId = 999)

        assertThrows<EntityNotFoundException> {
            service.createEmployee(request)
        }

        verify(exactly = 1) { sectorRepo.findById(999) }
        verify(exactly = 0) { employeeRepo.save(any<Employee>()) }
    }

    @Test
    fun `editEmployee should update employee with new sector when both exist`() {
        val employeeId = 7
        val oldSector = Sector(id = 1, name = "Old Sector")
        val newSector = Sector(id = 2, name = "New Sector")

        val oldEmployee = Employee(
            id = employeeId,
            name = "Old",
            sector = oldSector
        )

        val sectorRepo = spyk(
            FakeSectorRepository { id ->
                if (id == 2) Optional.of(newSector) else Optional.empty()
            }
        )

        var savedArg: Employee? = null
        val employeeRepo = spyk(
            FakeEmployeeRepository(
                findByIdAnswer = { id -> if (id == employeeId) Optional.of(oldEmployee) else Optional.empty() },
                saveAnswer = { e ->
                    savedArg = e
                    e
                }
            )
        )

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        val request = EmployeeRequest(name = "New Name", sectorId = 2)

        val result = service.editEmployee(employeeId to request)

        assertSame(savedArg, result)
        assertEquals(employeeId, result.id)
        assertEquals("New Name", result.name)
        assertSame(newSector, result.sector)

        verify(exactly = 1) { employeeRepo.findById(employeeId) }
        verify(exactly = 1) { sectorRepo.findById(2) }
        verify(exactly = 1) { employeeRepo.save(any<Employee>()) }
    }

    @Test
    fun `editEmployee should throw when employee does not exist on update`() {
        val sectorRepo = spyk(FakeSectorRepository { Optional.empty() })
        val employeeRepo = spyk(FakeEmployeeRepository(findByIdAnswer = { Optional.empty() }))

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        val request = EmployeeRequest(name = "X", sectorId = 1)

        assertThrows<EntityNotFoundException> {
            service.editEmployee(404 to request)
        }

        verify(exactly = 1) { employeeRepo.findById(404) }
        verify(exactly = 0) { sectorRepo.findById(any<Int>()) }
        verify(exactly = 0) { employeeRepo.save(any<Employee>()) }
    }

    @Test
    fun `editEmployee should throw when sector does not exist on update`() {
        val employeeId = 8
        val oldSector = Sector(id = 1, name = "Old Sector")
        val oldEmployee = Employee(id = employeeId, name = "Old", sector = oldSector)

        val sectorRepo = spyk(FakeSectorRepository { Optional.empty() })
        val employeeRepo = spyk(
            FakeEmployeeRepository(
                findByIdAnswer = { id -> if (id == employeeId) Optional.of(oldEmployee) else Optional.empty() }
            )
        )

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        val request = EmployeeRequest(name = "New", sectorId = 999)

        assertThrows<EntityNotFoundException> {
            service.editEmployee(employeeId to request)
        }

        verify(exactly = 1) { employeeRepo.findById(employeeId) }
        verify(exactly = 1) { sectorRepo.findById(999) }
        verify(exactly = 0) { employeeRepo.save(any<Employee>()) }
    }

    @Test
    fun `deleteEmployeeById should delete employee when exists`() {
        val employeeId = 9
        val sector = Sector(id = 1, name = "Sector")
        val employee = Employee(id = employeeId, name = "A", sector = sector)

        var deleted: Employee? = null
        val employeeRepo = spyk(
            FakeEmployeeRepository(
                findByIdAnswer = { id -> if (id == employeeId) Optional.of(employee) else Optional.empty() },
                deleteAnswer = { e -> deleted = e }
            )
        )
        val sectorRepo = spyk(FakeSectorRepository { Optional.empty() })

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        service.deleteEmployeeById(employeeId)

        assertSame(employee, deleted)
        verify(exactly = 1) { employeeRepo.findById(employeeId) }
        verify(exactly = 1) { employeeRepo.delete(employee) }
    }

    @Test
    fun `deleteEmployeeById should throw when deleting non-existent employee`() {
        val employeeRepo = spyk(FakeEmployeeRepository(findByIdAnswer = { Optional.empty() }))
        val sectorRepo = spyk(FakeSectorRepository { Optional.empty() })

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        assertThrows<EntityNotFoundException> {
            service.deleteEmployeeById(999)
        }

        verify(exactly = 1) { employeeRepo.findById(999) }
        verify(exactly = 0) { employeeRepo.delete(any<Employee>()) }
    }

    @Test
    fun `getAllEmployee should return list from repository`() {
        val sector = Sector(id = 1, name = "Sector")
        val expected = listOf(
            Employee(id = 1, name = "A", sector = sector),
            Employee(id = 2, name = "B", sector = sector)
        )

        val employeeRepo = spyk(
            FakeEmployeeRepository(
                findByIdAnswer = { Optional.empty() },
                findAllAnswer = { expected }
            )
        )
        val sectorRepo = spyk(FakeSectorRepository { Optional.empty() })

        val service = EmployeeService(
            employeeRepository = employeeRepo,
            sectorRepository = sectorRepo
        )

        val result = service.getAllEmployee()

        assertEquals(expected, result)
        verify(exactly = 1) { employeeRepo.findAll() }
    }
}
