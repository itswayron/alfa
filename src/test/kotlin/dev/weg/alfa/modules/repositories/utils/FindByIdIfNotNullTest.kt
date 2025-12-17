package dev.weg.alfa.modules.repositories.utils

import BaseTest
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.*

class FindByIdIfNotNullTest : BaseTest() {

    data class DummyEntity(val id: Int, val name: String)

    private fun attachListAppenderToRootLogger(): ListAppender<ILoggingEvent> {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        root.addAppender(appender)
        return appender
    }

    @Test
    fun `findByIdIfNotNull should return null when id is null and never call repository`() {
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.empty() })

        val result = repo.findByIdIfNotNull<DummyEntity, Int>(null)

        assertNull(result)
        verify(exactly = 0) { repo.findById(any()) }
    }

    @Test
    fun `findByIdIfNotNull should return entity when repository returns value`() {
        val entity = DummyEntity(id = 10, name = "ok")
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.of(entity) })

        val result = repo.findByIdIfNotNull<DummyEntity, Int>(10)

        assertEquals(entity, result)
        verify(exactly = 1) { repo.findById(10) }
    }

    @Test
    fun `findByIdIfNotNull should not log warning when entity is found`() {
        val appender = attachListAppenderToRootLogger()
        val entity = DummyEntity(id = 7, name = "found")
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.of(entity) })

        val result = repo.findByIdIfNotNull<DummyEntity, Int>(7)

        assertEquals(entity, result)
        verify(exactly = 1) { repo.findById(7) }

        val warnEvents = appender.list
            .filter { it.level == Level.WARN }
            .filter { it.formattedMessage.contains("was not found (nullable lookup)") }

        assertTrue(
            warnEvents.isEmpty(),
            "Did not expect WARN log when entity is found, but got:\n${
                warnEvents.joinToString("\n") { it.loggerName + " -- " + it.formattedMessage }
            }"
        )
    }

    @Test
    fun `findByIdIfNotNull should return null when repository returns empty and log warning`() {
        val appender = attachListAppenderToRootLogger()
        val repo = spyk(FakeJpaRepository<DummyEntity, Int> { Optional.empty() })

        val result = repo.findByIdIfNotNull<DummyEntity, Int>(99)

        assertNull(result)
        verify(exactly = 1) { repo.findById(99) }

        val warnEvents = appender.list
            .filter { it.level == Level.WARN }
            .filter { it.formattedMessage.contains("was not found (nullable lookup)") }

        assertTrue(
            warnEvents.isNotEmpty(),
            "Expected at least one WARN log event about nullable lookup not found"
        )

        val joined = warnEvents.joinToString("\n") { it.loggerName + " -- " + it.formattedMessage }
        assertTrue(
            joined.contains("DummyEntity") && joined.contains("99"),
            "Expected WARN to mention entity name and id. Logs were:\n$joined"
        )
    }
}
