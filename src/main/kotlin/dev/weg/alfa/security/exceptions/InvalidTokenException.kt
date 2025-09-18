package dev.weg.alfa.security.exceptions

import dev.weg.alfa.modules.exceptions.ApiException
import dev.weg.alfa.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class InvalidTokenException() : IllegalArgumentException(), ApiException {
  override val apiMessage: String = ExceptionErrorMessages.INVALID_RESET_PASSWORD_TOKEN.message
  override val status: HttpStatus = HttpStatus.BAD_REQUEST
  override val details: List<String?> = emptyList()
}
