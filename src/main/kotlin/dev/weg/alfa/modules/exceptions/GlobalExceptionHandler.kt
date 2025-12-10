package dev.weg.alfa.modules.exceptions

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class GlobalExceptionHandler {

    // TODO: Unit Test : Should return ApiError from ApiException using toApiError() with correct status
    // TODO: Unit Test : Should return 404 ApiError when handling EntityNotFoundException
    // TODO: Unit Test : Should return 500 ApiError when handling non-ApiException and non-EntityNotFoundException
    // TODO: Unit Test : Should include exception message in ApiError.details for generic exception in handleCustomExceptions
    // TODO: Unit Test : Should return 500 ApiError with localizedMessage in details when calling handleUnknownException
    // TODO: Unit Test : Should return 413 ApiError with custom file-size message when handling MaxUploadSizeExceededException

    // TODO: Integration Test : Should map ApiException thrown in controller to correct JSON ApiError via GlobalExceptionHandler
    // TODO: Integration Test : Should map EntityNotFoundException to 404 JSON ApiError
    // TODO: Integration Test : Should map unexpected RuntimeException to 500 JSON ApiError using handleUnknownException
    // TODO: Integration Test : Should map MaxUploadSizeExceededException to 413 JSON ApiError


    @ExceptionHandler(
        EntityNotFoundException::class
    )
    @ResponseBody
    fun handleCustomExceptions(exception: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        return if (exception is ApiException) {
            val apiError = exception.toApiError(request)
            ResponseEntity(apiError, HttpStatus.valueOf(apiError.status))
        } else {
            val status = if(exception is EntityNotFoundException) {
                HttpStatus.NOT_FOUND
            } else {
                HttpStatus.INTERNAL_SERVER_ERROR
            }
            val apiError = ApiError(
                status = status.value(),
                error = status.reasonPhrase,
                message = exception.message ?: "An error occurred.",
                path = request.requestURI,
                details = arrayListOf(exception.message),
            )
            ResponseEntity(apiError, status)
        }
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleUnknownException(exception: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val apiError = ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = exception.message ?: "An unexpected error occurred.",
            path = request.requestURI,
            details = arrayListOf(exception.localizedMessage)
        )
        return ResponseEntity(apiError, status)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    @ResponseBody
    fun handleMaxUploadSizeExceededException(
        exception: MaxUploadSizeExceededException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.PAYLOAD_TOO_LARGE
        val apiError = ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = "The uploaded file exceeds the maximum allowed size of 5MB.",
            path = request.requestURI,
            details = arrayListOf(exception.localizedMessage)
        )
        return ResponseEntity(apiError, status)
    }
}
