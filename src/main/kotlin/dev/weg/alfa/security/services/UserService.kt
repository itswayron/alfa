package dev.weg.alfa.security.services

import dev.weg.alfa.modules.exceptions.user.UserNotFoundException
import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.modules.models.user.UserRequest
import dev.weg.alfa.modules.models.user.UserResponse
import dev.weg.alfa.modules.repositories.user.UserRepository
import dev.weg.alfa.modules.repositories.user.getCurrentUser
import dev.weg.alfa.modules.validators.Validator
import dev.weg.alfa.security.validators.UserPersistenceValidator
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val encoder: PasswordEncoder,
    private val userRequestValidator: Validator<UserRequest>,
    private val persistenceValidator: UserPersistenceValidator,
    private val passwordValidator: Validator<String>
) : UserDetailsService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        return repository.findByUsernameField(usernameOrEmail)
            ?: repository.findByEmailField(usernameOrEmail)
            ?: throw UserNotFoundException(usernameOrEmail)
    }

    fun createUser(request: UserRequest): UserResponse {
        val sanitizedRequest = request.sanitized()
        logger.info("Creating user with username: ${sanitizedRequest.name}")

        userRequestValidator.validate(sanitizedRequest)
        passwordValidator.validate(sanitizedRequest.password)

        val user = User(
            name = sanitizedRequest.name,
            emailField = sanitizedRequest.email,
            passwordField = encoder.encode(sanitizedRequest.password),
            usernameField = sanitizedRequest.username,
        )
        persistenceValidator.validateNewUser(user)

        repository.save(user)

        logger.info("User created with id: ${user.id}")
        val response = user.toResponse()
        return response
    }

    fun findUserById(id: Int): UserResponse {
        logger.info("Fetching user with id: $id")
        val user = repository.findById(id).orElseThrow()
        logger.info("Retrieved the book with ID: $id - Username: ${user.usernameField}")
        val userResponse = user.toResponse()
        return userResponse
    }

    fun findCurrentUser(): UserResponse =
        repository.getCurrentUser().toResponse()

    private fun UserRequest.sanitized(): UserRequest =
        this.copy(
            name = this.name.trim(),
            username = this.username.trim(),
            email = this.email.trim(),
        )

    private fun User.toResponse(): UserResponse =
        UserResponse(
            id = this.id,
            name = this.name,
            username = this.usernameField,
            email = this.emailField,
            createdAt = this.createdAt,
        )
}
