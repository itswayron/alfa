package dev.weg.alfa.security.validators

import dev.weg.alfa.modules.exceptions.user.UserNotValidException
import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.modules.repositories.user.UserRepository
import dev.weg.alfa.modules.validators.ValidationErrorMessages
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserPersistenceValidator(private val repository: UserRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun validateNewUser(user: User) {
        logger.info("Validating avaliability of username and email for user: {}", user.usernameField)
        val errors = mutableListOf<String>()

        validateUsernameIsAvailable(user.usernameField, errors)
        validateEmailIsAvailable(user.emailField, errors)

        if (errors.isNotEmpty()) {
            logger.error("User data not available (username/email conflict): {}", user.usernameField)
            throw UserNotValidException(user.usernameField, errors)
        }
        logger.info("Valid user: {}", user.usernameField)
    }

    private fun validateUsernameIsAvailable(username: String, errors: MutableList<String>) {
        logger.debug("Validating if username is available.")
        if (repository.existsByUsernameField(username)) {
            logger.error("Username is not available.")
            errors.add(ValidationErrorMessages.USERNAME_NOT_AVAILABLE.message)
        } else {
            logger.debug("Username {} is available.", username)
        }
    }

    private fun validateEmailIsAvailable(email: String, errors: MutableList<String>) {
        logger.debug("Validating if email is available.")
        if (repository.existsByEmailField(email)) {
            logger.error("Email is not available.")
            errors.add(ValidationErrorMessages.EMAIL_NOT_AVAILABLE.message)
        } else {
            logger.debug("Email {} is available.", email)
        }
    }
}
