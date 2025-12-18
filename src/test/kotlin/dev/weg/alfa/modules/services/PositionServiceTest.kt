package dev.weg.alfa.modules.services

import BaseTest
import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.position.PositionPatch
import dev.weg.alfa.modules.repositories.PositionRepository
import dev.weg.alfa.modules.repositories.utils.FakeJpaRepository
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class PositionServiceTest : BaseTest() {

    private class FakePositionRepository(
        findByIdAnswer: (Int) -> Optional<Position>,
        private val saveAnswer: (Position) -> Position = { it },
        private val findAllAnswer: () -> List<Position> = { emptyList() },
        private val deleteAnswer: (Position) -> Unit = {}
    ) : FakeJpaRepository<Position, Int>(findByIdAnswer), PositionRepository {

        override fun <S : Position> save(entity: S): S {
            @Suppress("UNCHECKED_CAST")
            return saveAnswer(entity) as S
        }

        override fun findAll(): MutableList<Position> = findAllAnswer().toMutableList()

        override fun delete(entity: Position) {
            deleteAnswer(entity)
        }
    }

    @Test
    fun `updatePosition should update when position exists`() {
        val old = Position(
            id = 1,
            floor = "F1",
            side = "L",
            column = "C1",
            box = "B1"
        )

        val repo = spyk(
            FakePositionRepository(
                findByIdAnswer = { id -> if (id == 1) Optional.of(old) else Optional.empty() },
                saveAnswer = { it }
            )
        )
        val service = PositionService(repository = repo)

        val patch = PositionPatch(
            floor = "F2",
            side = null,
            column = "C2",
            box = null
        )

        val updated = service.updatePosition(1 to patch)

        assertEquals(
            Position(id = 1, floor = "F2", side = "L", column = "C2", box = "B1"),
            updated
        )

        verify(exactly = 1) { repo.findById(1) }
        verify(exactly = 1) { repo.save(Position(id = 1, floor = "F2", side = "L", column = "C2", box = "B1")) }
    }

    @Test
    fun `updatePosition should throw when updating non-existent position`() {
        val repo = spyk(
            FakePositionRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { it }
            )
        )
        val service = PositionService(repository = repo)

        val patch = PositionPatch(floor = "F", side = null, column = null, box = null)

        try {
            service.updatePosition(999 to patch)
            throw AssertionError("Expected EntityNotFoundException")
        } catch (_: EntityNotFoundException) {
            // expected
        }

        verify(exactly = 1) { repo.findById(999) }
    }

    @Test
    fun `deletePosition should delete position when it exists`() {
        val old = Position(
            id = 5,
            floor = "F",
            side = "S",
            column = "C",
            box = "B"
        )

        var deletedId: Int? = null
        val repo = spyk(
            FakePositionRepository(
                findByIdAnswer = { id -> if (id == 5) Optional.of(old) else Optional.empty() },
                saveAnswer = { it },
                deleteAnswer = { entity -> deletedId = entity.id }
            )
        )
        val service = PositionService(repository = repo)

        service.deletePositionById(5)

        assertEquals(5, deletedId)
        verify(exactly = 1) { repo.findById(5) }
        verify(exactly = 1) { repo.delete(old) }
    }

    @Test
    fun `deletePosition should throw when deleting non-existent position`() {
        val repo = spyk(
            FakePositionRepository(
                findByIdAnswer = { Optional.empty() },
                saveAnswer = { it }
            )
        )
        val service = PositionService(repository = repo)

        try {
            service.deletePositionById(404)
            throw AssertionError("Expected EntityNotFoundException")
        } catch (_: EntityNotFoundException) {
            // expected
        }

        verify(exactly = 1) { repo.findById(404) }
    }
}
