package dev.weg.alfa.modules.services

import BaseTest
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.businessPartner.BusinessPartnerPatch
import dev.weg.alfa.modules.repositories.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.utils.FakeJpaRepository
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertSame

class BusinessPartnerServiceTest : BaseTest() {

    private class FakeBusinessPartnerRepository(
        findByIdAnswer: (Int) -> Optional<BusinessPartner>,
        private val saveAnswer: (BusinessPartner) -> BusinessPartner,
        private val findAllAnswer: () -> List<BusinessPartner> = { emptyList() },
        private val deleteByIdAnswer: (Int) -> Unit = {}
    ) : FakeJpaRepository<BusinessPartner, Int>(findByIdAnswer), BusinessPartnerRepository {

        override fun <S : BusinessPartner> save(entity: S): S {
            @Suppress("UNCHECKED_CAST")
            return saveAnswer(entity) as S
        }

        override fun findAll(): MutableList<BusinessPartner> = findAllAnswer().toMutableList()

        override fun deleteById(id: Int) {
            deleteByIdAnswer(id)
        }
    }

    @AfterEach
    fun clearAuditContext() {
        AuditContext.consume()
    }

    @Test
    fun `createPartner should sanitize BusinessPartner fields on create`() {
        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { it.copy(id = 1) }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        val request = BusinessPartner(
            id = 0,
            name = "  ACME LTDA  ",
            cnpj = "  12.345.678/0001-90  ",
            relation = "  Supplier  "
        )

        val created = service.createPartner(request)

        assertEquals(1, created.id)
        assertEquals("ACME LTDA", created.name)
        assertEquals("12.345.678/0001-90", created.cnpj)
        assertEquals("Supplier", created.relation)

        verify(exactly = 1) {
            repo.save(
                BusinessPartner(
                    name = "ACME LTDA",
                    cnpj = "12.345.678/0001-90",
                    relation = "Supplier"
                )
            )
        }
    }

    @Test
    fun `updatePartner should apply patch updating only non-null fields`() {
        val old = BusinessPartner(
            id = 10,
            name = "Old Name",
            cnpj = "old-cnpj",
            relation = "old-relation"
        )

        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { id -> if (id == 10) Optional.of(old) else Optional.empty() },
                saveAnswer = { it }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        val patch = BusinessPartnerPatch(
            name = "New Name",
            cnpj = null,
            relation = "new-relation"
        )

        val updated = service.updatePartner(10 to patch)

        assertEquals(
            BusinessPartner(
                id = 10,
                name = "New Name",
                cnpj = "old-cnpj",
                relation = "new-relation"
            ),
            updated
        )

        verify(exactly = 1) { repo.findById(10) }
        verify(exactly = 1) {
            repo.save(
                BusinessPartner(
                    id = 10,
                    name = "New Name",
                    cnpj = "old-cnpj",
                    relation = "new-relation"
                )
            )
        }
    }

    @Test
    fun `updatePartner should preserve old values when patch fields are all null`() {
        val old = BusinessPartner(
            id = 11,
            name = "Keep Name",
            cnpj = "keep-cnpj",
            relation = "keep-relation"
        )

        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { id -> if (id == 11) Optional.of(old) else Optional.empty() },
                saveAnswer = { it }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        val patch = BusinessPartnerPatch(
            name = null,
            cnpj = null,
            relation = null
        )

        val updated = service.updatePartner(11 to patch)

        assertEquals(old, updated)

        verify(exactly = 1) { repo.findById(11) }
        verify(exactly = 1) { repo.save(old) }
    }

    @Test
    fun `findPartnerById should throw when partner ID does not exist`() {
        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { it }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        assertThrows<EntityNotFoundException> {
            service.findPartnerById(999)
        }

        verify(exactly = 1) { repo.findById(999) }
    }

    @Test
    fun `deletePartner should delete partner by ID using repository`() {
        val existing = BusinessPartner(
            id = 123,
            name = "Partner",
            cnpj = "1",
            relation = "R"
        )
        var deletedId: Int? = null
        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { id ->
                    if (id == 123) Optional.of(existing) else Optional.empty()
                },
                saveAnswer = { it },
                deleteByIdAnswer = { id -> deletedId = id }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        service.deletePartner(123)

        assertEquals(123, deletedId)
        verify(exactly = 1) { repo.deleteById(123) }
    }

    @Test
    fun `findAllPartners should return repository list`() {
        val expected = listOf(
            BusinessPartner(id = 1, name = "A", cnpj = "1", relation = "r1"),
            BusinessPartner(id = 2, name = "B", cnpj = "2", relation = "r2")
        )

        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { it },
                findAllAnswer = { expected }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        val result = service.findAllPartners()

        assertEquals(expected, result)
        verify(exactly = 1) { repo.findAll() }
    }

    @Test
    fun `createPartner should return the saved entity returned by repository`() {
        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { saved ->
                    saved.copy(id = 777)
                }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        val request = BusinessPartner(
            id = 0,
            name = "  Partner  ",
            cnpj = "  1  ",
            relation = "  R  "
        )

        val result = service.createPartner(request)

        assertEquals(777, result.id)
        assertEquals("Partner", result.name)
        assertEquals("1", result.cnpj)
        assertEquals("R", result.relation)

        verify(exactly = 1) {
            repo.save(
                BusinessPartner(
                    name = "Partner",
                    cnpj = "1",
                    relation = "R"
                )
            )
        }
    }

    @Test
    fun `updatePartner should save updated entity exactly once`() {
        val old = BusinessPartner(
            id = 22,
            name = "Old",
            cnpj = "old",
            relation = "old"
        )

        var savedArg: BusinessPartner? = null
        val repo = spyk(
            FakeBusinessPartnerRepository(
                findByIdAnswer = { id -> if (id == 22) Optional.of(old) else Optional.empty() },
                saveAnswer = { entity ->
                    savedArg = entity
                    entity
                }
            )
        )
        val service = BusinessPartnerService(repository = repo)

        val patch = BusinessPartnerPatch(
            name = "New",
            cnpj = null,
            relation = null
        )

        val result = service.updatePartner(22 to patch)

        verify(exactly = 1) { repo.findById(22) }
        verify(exactly = 1) { repo.save(any<BusinessPartner>()) }

        assertSame(savedArg, result, "Service should return the same instance it saved (current behavior)")
        assertEquals(22, result.id)
        assertEquals("New", result.name)
        assertEquals("old", result.cnpj)
        assertEquals("old", result.relation)
    }
}
