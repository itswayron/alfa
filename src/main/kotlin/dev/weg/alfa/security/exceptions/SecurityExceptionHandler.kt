package dev.weg.alfa.security.exceptions

import dev.weg.alfa.modules.exceptions.ApiError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class SecurityExceptionHandler {

    @ExceptionHandler(
        BadCredentialsException::class,
        AuthenticationException::class,
        InternalAuthenticationServiceException::class,
    )
    @ResponseBody
    fun handleAuthExceptions(
        exception: AuthenticationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiError(
                status = HttpStatus.UNAUTHORIZED.value(),
                error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                message = "Authentication failed.",
                path = request.requestURI,
                details = listOf("Bad credentials")
            )
        )

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseBody
    fun handleAccessDeniedException(
        exception: AccessDeniedException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ApiError(
                status = HttpStatus.FORBIDDEN.value(),
                error = HttpStatus.FORBIDDEN.reasonPhrase,
                message = "User does not have permission to perform this action.",
                path = request.requestURI
            )
        )
}
