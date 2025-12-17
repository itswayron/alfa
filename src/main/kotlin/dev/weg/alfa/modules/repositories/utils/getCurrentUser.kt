package dev.weg.alfa.modules.repositories.utils

import dev.weg.alfa.modules.exceptions.user.UserNotFoundException
import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.modules.repositories.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder

fun UserRepository.getCurrentUser(): User {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val username = SecurityContextHolder.getContext().authentication.name
    logger.info("Searching for user: $username")
    val user = this.findByUsernameField(username)

    return if (user != null) {
        logger.info("User $username found.")
        user
    } else {
        logger.error("User $username not found.")
        throw UserNotFoundException(username)
    }
}
