package dev.weg.alfa.security.models.password

data class ResetPasswordRequest(
  val token: String,
  val newPassword: String,
)
