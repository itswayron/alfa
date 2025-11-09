package dev.weg.alfa.security.services

import dev.weg.alfa.modules.exceptions.user.UserNotFoundException
import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.modules.models.user.UserRequest
import dev.weg.alfa.modules.models.user.UserResponse
import dev.weg.alfa.modules.repositories.user.UserRepository
import dev.weg.alfa.modules.repositories.utils.getCurrentUser
import dev.weg.alfa.modules.validators.Validator
import dev.weg.alfa.security.config.SecurityLogger
import dev.weg.alfa.security.validators.UserPersistenceValidator
import dev.weg.alfa.utils.maskLast
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

    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        SecurityLogger.log.debug("Attempting to load user by usernameOrEmail='{}'", usernameOrEmail)
        val user = repository.findByUsernameField(usernameOrEmail)
            ?: repository.findByEmailField(usernameOrEmail)
            ?: run {
                SecurityLogger.log.warn("User lookup failed for identifier='{}'", usernameOrEmail)
                throw UserNotFoundException(usernameOrEmail)
            }

        SecurityLogger.log.debug("User '{}' successfully loaded from repository", user.usernameField)
        return user
    }

    fun createUser(request: UserRequest): UserResponse {
        val sanitizedRequest = request.sanitized()
        SecurityLogger.log.info(
            "User creation requested for username='{}', email='{}'",
            sanitizedRequest.username,
            sanitizedRequest.email.maskLast(6)
        )

        userRequestValidator.validate(sanitizedRequest)
        passwordValidator.validate(sanitizedRequest.password)

        val user = User(
            name = sanitizedRequest.name,
            emailField = sanitizedRequest.email,
            passwordField = encoder.encode(sanitizedRequest.password),
            usernameField = sanitizedRequest.username,
        )

        persistenceValidator.validateNewUser(user)
        val savedUser = repository.save(user)

        SecurityLogger.log.info(
            "User created successfully with id='{}', username='{}'",
            savedUser.id,
            savedUser.usernameField
        )

        return savedUser.toResponse()
    }

    fun findUserById(id: Int): UserResponse {
        SecurityLogger.log.debug("Fetching user by id='{}'", id)

        val user = repository.findById(id).orElseThrow {
            SecurityLogger.log.warn("User not found for id='{}'", id)
            UserNotFoundException("User id=$id not found")
        }

        SecurityLogger.log.info("User retrieved: id='{}', username='{}'", user.id, user.usernameField)
        return user.toResponse()
    }

    fun findCurrentUser(): UserResponse {
        SecurityLogger.log.debug("Fetching currently authenticated user")
        val user = repository.getCurrentUser()
        SecurityLogger.log.info("Current authenticated user='{}'", user.usernameField)
        return user.toResponse()
    }

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
