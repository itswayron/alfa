package dev.weg.alfa.infra.exceptions

import dev.weg.alfa.modules.exceptions.ApiException
import dev.weg.alfa.modules.exceptions.ExceptionErrorMessages
import org.springframework.http.HttpStatus

class ImageNotValidException(val errors: List<String>) : RuntimeException(), ApiException {
    override val apiMessage: String = ExceptionErrorMessages.INVALID_IMAGE.message
    override val status: HttpStatus = HttpStatus.BAD_REQUEST
    override val details: List<String?> = errors
}
