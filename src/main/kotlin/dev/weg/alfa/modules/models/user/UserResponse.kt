package dev.weg.alfa.modules.models.user

import java.time.LocalDateTime

data class UserResponse(
  val id: Int,
  val username: String,
  val name: String? = null,
  val email: String,
  val createdAt: LocalDateTime
)
