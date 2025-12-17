package dev.weg.alfa.modules.repositories.utils

import BaseTest
import dev.weg.alfa.modules.exceptions.user.UserNotFoundException
import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.modules.repositories.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

class GetCurrentUserTest : BaseTest() {

    private open class FakeUserRepository(
        private val findByUsernameAnswer: (String) -> User?
    ) : FakeJpaRepository<User, Int>({ Optional.empty() }), UserRepository {

        override fun findByUsernameField(usernameField: String): User? = findByUsernameAnswer(usernameField)

        override fun findByEmailField(email: String): User? =
            throw UnsupportedOperationException("Not needed for these tests")

        override fun existsByEmailField(email: String): Boolean =
            throw UnsupportedOperationException("Not needed for these tests")

        override fun existsByUsernameField(username: String): Boolean =
            throw UnsupportedOperationException("Not needed for these tests")

        override fun notFoundException(id: Int): EntityNotFoundException =
            EntityNotFoundException("User with id=$id does not exist.")
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `getCurrentUser should return user when username exists`() {
        val username = "john"

        val authentication = mockk<Authentication>()
        every { authentication.name } returns username

        val securityContext = mockk<SecurityContext>()
        every { securityContext.authentication } returns authentication

        SecurityContextHolder.setContext(securityContext)

        val expectedUser = mockk<User>(relaxed = true)
        val repo = spyk(FakeUserRepository { userName ->
            if (userName == username) expectedUser else null
        })

        val result = repo.getCurrentUser()

        assertSame(expectedUser, result)
        verify(exactly = 1) { repo.findByUsernameField(username) }
    }

    @Test
    fun `getCurrentUser should throw UserNotFoundException when username does not exist`() {
        val username = "missing"

        val authentication = mockk<Authentication>()
        every { authentication.name } returns username

        val securityContext = mockk<SecurityContext>()
        every { securityContext.authentication } returns authentication

        SecurityContextHolder.setContext(securityContext)

        val repo = spyk(FakeUserRepository { null })

        assertThrows<UserNotFoundException> {
            repo.getCurrentUser()
        }

        verify(exactly = 1) { repo.findByUsernameField(username) }
    }

    @Test
    fun `getCurrentUser should throw when authentication is missing (current behavior)`() {
        val securityContext = mockk<SecurityContext>()
        every { securityContext.authentication } returns null

        SecurityContextHolder.setContext(securityContext)

        val repo = spyk(FakeUserRepository { error("should not be called") })

        assertThrows<NullPointerException> {
            repo.getCurrentUser()
        }

        verify(exactly = 0) { repo.findByUsernameField(any()) }
    }

    @Test
    fun `getCurrentUser should throw when SecurityContextHolder has no authentication (default empty context)`() {
        val repo = spyk(FakeUserRepository { error("should not be called") })

        assertThrows<NullPointerException> {
            repo.getCurrentUser()
        }

        verify(exactly = 0) { repo.findByUsernameField(any()) }
    }
}
