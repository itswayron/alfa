package dev.weg.alfa.security.models

data class AuthenticationRequest(
  val username: String,
  val password: String,
)
