package dev.weg.alfa.security.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.security.config.SecurityLogger
import dev.weg.alfa.security.models.AuthenticationRequest
import dev.weg.alfa.security.models.password.ForgotPasswordRequest
import dev.weg.alfa.security.models.password.ResetPasswordRequest
import dev.weg.alfa.security.services.AuthenticationService
import dev.weg.alfa.security.services.PasswordResetService
import dev.weg.alfa.utils.maskLast
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.AUTH)
class AuthController(
    private val authenticationService: AuthenticationService,
    private val passwordResetService: PasswordResetService,
) {

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequest, response: HttpServletResponse) {
        SecurityLogger.log.info("Login attempt for user='{}'", authRequest.username)
        val token = authenticationService.authentication(authRequest)
        SecurityLogger.log.info("Login successful for user='{}'", authRequest.username)

        val cookie = Cookie("access_token", token.accessToken).apply {
            isHttpOnly = true
            path = "/"
            secure = false
            maxAge = 60 * 60 * 100
        }

        response.addCookie(cookie)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {
        SecurityLogger.log.info("User logout request received")
        val expiredCookie = Cookie("access_token", "").apply {
            isHttpOnly = true
            path = "/"
            secure = false
            maxAge = 0
        }

        response.addCookie(expiredCookie)
        response.status = HttpServletResponse.SC_NO_CONTENT
        SecurityLogger.log.info("User logged out successfully")
    }

    @GetMapping("/status")
    fun checkStatus(): ResponseEntity<String> {
        SecurityLogger.log.debug("Authentication status check requested")
        return ResponseEntity.ok("User authenticated.")
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<Unit> {
        SecurityLogger.log.warn("Password reset requested for email='{}'", request.email.maskLast(5))
        passwordResetService.forgotPassword(request)
        SecurityLogger.log.info("Password reset email sent to '{}'", request.email.maskLast(5))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<Unit> {
        SecurityLogger.log.warn("Password reset attempt for token='{}'", request.token.maskLast(6))
        passwordResetService.resetPassword(request)
        SecurityLogger.log.info("Password successfully reset for token='{}'", request.token.maskLast(6))
        return ResponseEntity.ok().build()
    }
}
