package dev.weg.alfa.security.exceptions

import dev.weg.alfa.modules.exceptions.ApiException
import dev.weg.alfa.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class InvalidPasswordException(val errors: List<String> = emptyList()) : IllegalArgumentException(), ApiException {
  override val apiMessage: String = ExceptionErrorMessages.INVALID_PASSWORD.message
  override val status: HttpStatus = HttpStatus.BAD_REQUEST
  override val details: List<String?> = errors
}