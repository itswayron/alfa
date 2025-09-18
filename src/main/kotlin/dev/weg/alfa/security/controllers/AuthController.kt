package dev.weg.alfa.security.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.security.models.AuthenticationRequest
import dev.weg.alfa.security.models.password.ForgotPasswordRequest
import dev.weg.alfa.security.models.password.ResetPasswordRequest
import dev.weg.alfa.security.services.AuthenticationService
import dev.weg.alfa.security.services.PasswordResetService
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
    val token = authenticationService.authentication(authRequest)

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
    val expiredCookie = Cookie("access_token", "").apply {
      isHttpOnly = true
      path = "/"
      secure = false
      maxAge = 0
    }

    response.addCookie(expiredCookie)
    response.status = HttpServletResponse.SC_NO_CONTENT
  }

  @GetMapping("/status")
  fun checkStatus(): ResponseEntity<String> {
    return ResponseEntity.ok("User authenticated.")
  }

  @PostMapping("/forgot-password")
  fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<Unit> {
    passwordResetService.forgotPassword(request)
    return ResponseEntity.ok().build()
  }

  @PostMapping("/reset-password")
  fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<Unit> {
    passwordResetService.resetPassword(request)
    return ResponseEntity.ok().build()
  }
}
