package dev.weg.alfa.security.services

import dev.weg.alfa.modules.repositories.UserRepository
import dev.weg.alfa.modules.services.EmailService
import dev.weg.alfa.modules.validators.Validator
import dev.weg.alfa.security.exceptions.InvalidTokenException
import dev.weg.alfa.security.exceptions.TokenExpiredException
import dev.weg.alfa.security.models.password.ForgotPasswordRequest
import dev.weg.alfa.security.models.password.PasswordResetToken
import dev.weg.alfa.security.models.password.ResetPasswordRequest
import dev.weg.alfa.security.repositories.PasswordResetTokenRepository
import dev.weg.alfa.utils.Base62UUIDGenerator
import dev.weg.alfa.utils.maskLast
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PasswordResetService(
  private val emailService: EmailService,
  private val userRepository: UserRepository,
  private val tokenRepository: PasswordResetTokenRepository,
  private val encoder: PasswordEncoder,
  private val passwordValidator: Validator<String>
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  @Transactional
  fun forgotPassword(request: ForgotPasswordRequest) {
    val email = request.email
    logger.info("Searching for email: '{}'", email)

    val user = userRepository.findByEmailField(email)
    if (user == null) {
      logger.info("No user found with email: '{}', aborting password reset process.", email)
      return
    }
    logger.debug("User found for password reset: '{}'", user.usernameField)

    val token = Base62UUIDGenerator.generate()
    val passwordResetToken = PasswordResetToken(
      token = token, user = user, expireDate = LocalDateTime.now().plusMinutes(30)
    )
    logger.trace(
      "Generated password reset token: '{}', expiring at: '{}'",
      token.maskLast(),
      passwordResetToken.expireDate
    )

    tokenRepository.deleteByUser(user)
    logger.debug("Deleted all existing tokens for user: '{}'", user.usernameField)

    tokenRepository.save(passwordResetToken)
    logger.debug("Saved new password reset token for user: '{}'", user.usernameField)

    emailService.sendPasswordResetEmail(email = user.emailField, token = token)
  }

  @Transactional
  fun resetPassword(request: ResetPasswordRequest) {
    logger.info("Received password reset attempt for token: '{}'", request.token.maskLast())

    val token = findValidTokenOrThrow(request.token)
    val user = token.user

    logger.debug("Resetting password for user: '{}'. Old hash: '{}'", user.usernameField, user.password.maskLast())
    passwordValidator.validate(request.newPassword)
    user.passwordField = encoder.encode(request.newPassword)

    userRepository.save(user)
    logger.info("Password successfully updated for user: '{}'. New hash: '{}'", user.usernameField, user.password.maskLast())

    tokenRepository.deleteByUser(user)
    logger.debug("Deleted token after successful password reset for user: '{}'", user.usernameField)
  }

  private fun findValidTokenOrThrow(tokenString: String): PasswordResetToken {
    logger.trace("Searching for password reset token: '{}'", tokenString.maskLast())
    val token = tokenRepository.findByToken(tokenString)
    if (token == null) {
      logger.warn("Invalid password reset token received: '{}'", tokenString.maskLast())
      throw InvalidTokenException()
    }
    if (token.expireDate.isBefore(LocalDateTime.now())) {
      logger.warn("Expired password reset token: '{}', for user: '{}'", token.token.maskLast(), token.user.usernameField)
      tokenRepository.delete(token)
      throw TokenExpiredException()
    }
    logger.debug("Valid password reset token found: '{}', for user: '{}'", token.token.maskLast(), token.user.usernameField)
    return token
  }
}
