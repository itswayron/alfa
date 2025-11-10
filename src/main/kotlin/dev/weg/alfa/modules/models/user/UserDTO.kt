package dev.weg.alfa.modules.models.user

import java.time.LocalDateTime

data class UserRequest(
  val username: String,
  val name: String,
  val email: String,
  val password: String,
)

data class UserResponse(
  val id: Int,
  val username: String,
  val name: String? = null,
  val profilePath: String? = null,
  val email: String,
  val createdAt: LocalDateTime,
)
