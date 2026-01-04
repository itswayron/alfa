package dev.weg.alfa.modules.services

import BaseTest
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.models.tool.Tool
import dev.weg.alfa.modules.models.tool.ToolFilter
import dev.weg.alfa.modules.models.tool.ToolPatch
import dev.weg.alfa.modules.models.tool.ToolRequest
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.FakeJpaRepository
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.util.*

class ToolServiceTest : BaseTest() {

    private class FakeToolRepository(
        findByIdAnswer: (Int) -> Optional<Tool>,
        private val saveAnswer: (Tool) -> Tool = { it },
        private val deleteAnswer: (Tool) -> Unit = {},
        private val findAllAnswer: (Specification<Tool>, Pageable) -> Page<Tool> = { _, pageable ->
            PageImpl(
                emptyList(),
                pageable,
                0
            )
        }
    ) : FakeJpaRepository<Tool, Int>(findByIdAnswer), ToolRepository {

        override fun <S : Tool> save(entity: S): S {
            @Suppress("UNCHECKED_CAST")
            return saveAnswer(entity as Tool) as S
        }

        override fun findAll(spec: Specification<Tool>?, pageable: Pageable): Page<Tool?> {
            @Suppress("UNCHECKED_CAST")
            return findAllAnswer(spec as Specification<Tool>, pageable) as Page<Tool?>
        }

        override fun delete(entity: Tool) {
            deleteAnswer(entity)
        }
    }

    private class FakeSubgroupRepository(
        findByIdAnswer: (Int) -> Optional<Subgroup>
    ) : FakeJpaRepository<Subgroup, Int>(findByIdAnswer), SubgroupRepository

    @AfterEach
    fun clearAuditContext() {
        AuditContext.consume()
    }

    @Test
    fun `createTool should create when subgroup exists`() {
        val subgroup = Subgroup(id = 11, name = "SG")

        val subgroupRepo =
            spyk(FakeSubgroupRepository { id -> if (id == 11) Optional.of(subgroup) else Optional.empty() })

        var savedArg: Tool? = null
        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { t -> savedArg = t; t.copy(id = 99) }
            )
        )

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val req = ToolRequest(name = "Hammer", description = "desc", maximumUsages = 10, subgroupID = 11)
        val created = service.createTool(req)

        assertEquals(99, created.id)
        assertEquals("Hammer", created.name)
        assertEquals(11, created.subgroupID)

        assertNotNull(savedArg)
        assertEquals("Hammer", savedArg!!.name)
        assertEquals(0, savedArg!!.actualUsages)
        assertEquals(10, savedArg!!.maximumUsages)
        assertSame(subgroup, savedArg!!.subgroup)

        verify(exactly = 1) { subgroupRepo.findById(11) }
        verify(exactly = 1) { toolRepo.save(any<Tool>()) }
    }

    @Test
    fun `createTool should throw when subgroup does not exist`() {
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val toolRepo = spyk(FakeToolRepository(findByIdAnswer = { Optional.empty() }))

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val req = ToolRequest(name = "X", description = "d", maximumUsages = 1, subgroupID = 1234)

        assertThrows<EntityNotFoundException> {
            service.createTool(req)
        }

        verify(exactly = 1) { subgroupRepo.findById(1234) }
        verify(exactly = 0) { toolRepo.save(any<Tool>()) }
    }

    @Test
    fun `getFilteredTools should correctly map paginated tools to DTO`() {
        val subgroup = Subgroup(id = 2, name = "SG2")
        val tool1 = Tool(
            id = 1,
            name = "T1",
            description = "d1",
            maximumUsages = 5,
            actualUsages = 0,
            subgroup = subgroup,
            isLoan = false
        )
        val tool2 = Tool(
            id = 2,
            name = "T2",
            description = "d2",
            maximumUsages = 3,
            actualUsages = 1,
            subgroup = subgroup,
            isLoan = true
        )

        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { Optional.empty() },
                findAllAnswer = { _, pageable -> PageImpl(listOf(tool1, tool2), pageable, 2) }
            )
        )

        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val result = service.getFilteredTools(filter = ToolFilter(), pageable = PageRequest.of(0, 10))

        assertEquals(2, result.content.size)
        assertEquals(2, result.totalElements)
        assertEquals(1, result.totalPages)
        assertEquals(0, result.currentPage)
        assertEquals(10, result.pageSize)

        val r1 = result.content[0]
        assertEquals(1, r1.id)
        assertEquals("T1", r1.name)
        assertEquals(2, r1.subgroupID)

        verify(exactly = 1) { toolRepo.findAll(any<Specification<Tool>>(), any<Pageable>()) }
    }

    @Test
    fun `updateTool should update when exists and change subgroup when provided`() {
        val oldSub = Subgroup(id = 1, name = "Old")
        val newSub = Subgroup(id = 2, name = "New")

        val oldTool = Tool(
            id = 5,
            name = "OldTool",
            description = "d",
            maximumUsages = 5,
            actualUsages = 1,
            subgroup = oldSub,
            isLoan = false
        )

        val subgroupRepo = spyk(FakeSubgroupRepository { id -> if (id == 2) Optional.of(newSub) else Optional.empty() })

        var savedArg: Tool? = null
        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { id -> if (id == 5) Optional.of(oldTool) else Optional.empty() },
                saveAnswer = { t -> savedArg = t; t }
            )
        )

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val patch = ToolPatch(name = "Updated", description = null, maximumUsages = 7, subgroupID = 2, isLoan = true)

        val updated = service.updateTool(5 to patch)

        assertEquals(5, updated.id)
        assertEquals("Updated", updated.name)
        assertEquals(7, updated.maximumUsages)
        assertEquals(2, updated.subgroupID)
        assertTrue(updated.isLoan)

        assertNotNull(savedArg)
        assertEquals(5, savedArg!!.id)
        assertEquals("Updated", savedArg!!.name)
        assertSame(newSub, savedArg!!.subgroup)

        verify(exactly = 1) { toolRepo.findById(5) }
        verify(exactly = 1) { subgroupRepo.findById(2) }
        verify(exactly = 1) { toolRepo.save(any<Tool>()) }
    }

    @Test
    fun `updateTool should throw when tool does not exist`() {
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val toolRepo = spyk(FakeToolRepository(findByIdAnswer = { Optional.empty() }))

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val patch = ToolPatch(name = "X")

        assertThrows<EntityNotFoundException> {
            service.updateTool(999 to patch)
        }

        verify(exactly = 1) { toolRepo.findById(999) }
        verify(exactly = 0) { subgroupRepo.findById(any()) }
        verify(exactly = 0) { toolRepo.save(any<Tool>()) }
    }

    @Test
    fun `deleteToolById should delete when exists`() {
        val subgroup = Subgroup(id = 1, name = "S")
        val tool = Tool(
            id = 3,
            name = "T",
            description = "d",
            maximumUsages = 2,
            actualUsages = 0,
            subgroup = subgroup,
            isLoan = false
        )

        var deletedId: Int? = null
        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { id -> if (id == 3) Optional.of(tool) else Optional.empty() },
                deleteAnswer = { t -> deletedId = t.id }
            )
        )

        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        service.deleteToolById(3)

        assertEquals(3, deletedId)
        verify(exactly = 1) { toolRepo.findById(3) }
        verify(exactly = 1) { toolRepo.delete(any<Tool>()) }
    }

    @Test
    fun `deleteToolById should throw when tool does not exist`() {
        val toolRepo = spyk(FakeToolRepository(findByIdAnswer = { Optional.empty() }))
        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        assertThrows<EntityNotFoundException> {
            service.deleteToolById(404)
        }

        verify(exactly = 1) { toolRepo.findById(404) }
        verify(exactly = 0) { toolRepo.delete(any<Tool>()) }
    }

    @Test
    fun `getFilteredTools should return filtered tools using specification`() {
        val subgroup = Subgroup(id = 1, name = "SG1")
        val tool1 = Tool(
            id = 1,
            name = "Hammer",
            description = "d1",
            maximumUsages = 5,
            actualUsages = 0,
            subgroup = subgroup,
            isLoan = false
        )

        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { Optional.empty() },
                findAllAnswer = { _, pageable -> PageImpl(listOf(tool1), pageable, 1) }
            )
        )

        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })
        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val filter = ToolFilter()
        val result = service.getFilteredTools(filter = filter, pageable = PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals("Hammer", result.content[0].name)

        verify(exactly = 1) { toolRepo.findAll(any<Specification<Tool>>(), any<Pageable>()) }
    }

    @Test
    fun `updateTool should update tool with new subgroup when subgroupID changes`() {
        val oldSub = Subgroup(id = 1, name = "Old")
        val newSub = Subgroup(id = 2, name = "New")

        val oldTool = Tool(
            id = 10,
            name = "Hammer",
            description = "d",
            maximumUsages = 5,
            actualUsages = 0,
            subgroup = oldSub,
            isLoan = false
        )

        val subgroupRepo = spyk(FakeSubgroupRepository { id -> if (id == 2) Optional.of(newSub) else Optional.empty() })

        var savedArg: Tool? = null
        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { id -> if (id == 10) Optional.of(oldTool) else Optional.empty() },
                saveAnswer = { t -> savedArg = t; t }
            )
        )

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val patch = ToolPatch(name = "UpdatedHammer", description = null, maximumUsages = null, subgroupID = 2, isLoan = null)

        val updated = service.updateTool(10 to patch)

        assertEquals(10, updated.id)
        assertEquals("UpdatedHammer", updated.name)
        assertEquals(2, updated.subgroupID)
        assertNotNull(savedArg)
        assertSame(newSub, savedArg!!.subgroup)

        verify(exactly = 1) { toolRepo.findById(10) }
        verify(exactly = 1) { subgroupRepo.findById(2) }
        verify(exactly = 1) { toolRepo.save(any<Tool>()) }
    }

    @Test
    fun `updateTool should throw when updating tool with non-existent subgroup`() {
        val oldSub = Subgroup(id = 1, name = "Old")
        val oldTool = Tool(
            id = 20,
            name = "Screwdriver",
            description = "d",
            maximumUsages = 3,
            actualUsages = 0,
            subgroup = oldSub,
            isLoan = false
        )

        val subgroupRepo = spyk(FakeSubgroupRepository { Optional.empty() })

        val toolRepo = spyk(
            FakeToolRepository(
                findByIdAnswer = { id -> if (id == 20) Optional.of(oldTool) else Optional.empty() }
            )
        )

        val service = ToolService(toolRepository = toolRepo, subgroupRepository = subgroupRepo)

        val patch = ToolPatch(name = "Updated", description = null, maximumUsages = null, subgroupID = 999, isLoan = null)

        assertThrows<EntityNotFoundException> {
            service.updateTool(20 to patch)
        }

        verify(exactly = 1) { toolRepo.findById(20) }
        verify(exactly = 1) { subgroupRepo.findById(999) }
        verify(exactly = 0) { toolRepo.save(any<Tool>()) }
    }
}
