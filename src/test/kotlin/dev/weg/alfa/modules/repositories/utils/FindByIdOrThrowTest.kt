package dev.weg.alfa.modules.repositories.utils

import BaseTest
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import dev.weg.alfa.modules.exceptions.ExceptionProvider
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.util.Optional

class FindByIdOrThrowTest : BaseTest() {

    data class DummyEntity(val id: Int, val name: String)

    class DummyNotFoundException(message: String) : EntityNotFoundException(message)

    class FakeRepoWithExceptionProvider(
        findByIdAnswer: (Int) -> Optional<DummyEntity>,
        private val notFound: (Int) -> EntityNotFoundException
    ) : FakeJpaRepository<DummyEntity, Int>(findByIdAnswer), ExceptionProvider<Int> {
        override fun notFoundException(id: Int): EntityNotFoundException = notFound(id)
    }

    private fun attachListAppenderToRootLogger(): ListAppender<ILoggingEvent> {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        root.addAppender(appender)
        return appender
    }

    @Test
    fun `findByIdOrThrow should return entity when repository finds it`() {
        val entity = DummyEntity(id = 1, name = "ok")
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.of(entity) })

        val result = repo.findByIdOrThrow<DummyEntity, Int>(1)

        assertEquals(entity, result)
        verify(exactly = 1) { repo.findById(1) }
    }

    @Test
    fun `findByIdOrThrow should not log error when entity is found`() {
        val appender = attachListAppenderToRootLogger()
        val entity = DummyEntity(id = 5, name = "ok")
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.of(entity) })

        val result = repo.findByIdOrThrow<DummyEntity, Int>(5)

        assertEquals(entity, result)
        verify(exactly = 1) { repo.findById(5) }

        val errorEvents = appender.list
            .filter { it.level == Level.ERROR }
            .filter { it.formattedMessage.contains("does not exist") }

        assertTrue(
            errorEvents.isEmpty(),
            "Did not expect ERROR log when entity is found, but got:\n${
                errorEvents.joinToString("\n") { it.loggerName + " -- " + it.formattedMessage }
            }"
        )
    }

    @Test
    fun `findByIdOrThrow should call notFoundException on ExceptionProvider when entity is missing`() {
        val repo = spyk(
            FakeRepoWithExceptionProvider(
                findByIdAnswer = { Optional.empty() },
                notFound = { id -> DummyNotFoundException("custom not found id=$id") }
            )
        )

        assertThrows<DummyNotFoundException> {
            repo.findByIdOrThrow<DummyEntity, Int>(123)
        }

        verify(exactly = 1) { repo.findById(123) }
        verify(exactly = 1) { repo.notFoundException(123) }
    }

    @Test
    fun `findByIdOrThrow should throw custom exception when repository implements ExceptionProvider`() {
        val appender = attachListAppenderToRootLogger()
        val repo = spyk(
            FakeRepoWithExceptionProvider(
                findByIdAnswer = { Optional.empty() },
                notFound = { id -> DummyNotFoundException("custom not found id=$id") }
            )
        )

        val ex = assertThrows<DummyNotFoundException> {
            repo.findByIdOrThrow<DummyEntity, Int>(42)
        }
        assertEquals("custom not found id=42", ex.message)

        verify(exactly = 1) { repo.findById(42) }

        val errorEvents = appender.list
            .filter { it.level == Level.ERROR }
            .filter { it.formattedMessage.contains("DummyEntity with id: 42 does not exist.") }

        assertTrue(errorEvents.isNotEmpty(), "Expected an ERROR log with the not-found message when throwing")
    }

    @Test
    fun `findByIdOrThrow should throw EntityNotFoundException when repository does not implement ExceptionProvider`() {
        val appender = attachListAppenderToRootLogger()
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.empty() })

        val ex = assertThrows<EntityNotFoundException> {
            repo.findByIdOrThrow<DummyEntity, Int>(99)
        }
        assertTrue(ex.message!!.contains("DummyEntity with id: 99 does not exist."))

        verify(exactly = 1) { repo.findById(99) }

        val errorEvents = appender.list
            .filter { it.level == Level.ERROR }
            .filter { it.formattedMessage.contains("DummyEntity with id: 99 does not exist.") }

        assertTrue(
            errorEvents.isNotEmpty(),
            "Expected an ERROR log with the not-found message when throwing EntityNotFoundException"
        )
    }
}
