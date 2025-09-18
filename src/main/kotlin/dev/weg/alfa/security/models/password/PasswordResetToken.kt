package dev.weg.alfa.security.models.password

import dev.weg.alfa.modules.models.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class PasswordResetToken(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(nullable = false, unique = true)
  val token: String,

  @OneToOne
  val user: User,

  @Column(nullable = false)
  val expireDate: LocalDateTime
)
