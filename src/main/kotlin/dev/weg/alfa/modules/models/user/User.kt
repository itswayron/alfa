package dev.weg.alfa.modules.models.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "users")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int = 0,

  var name: String,

  @Column(unique = true)
  var usernameField: String,

  @Column(name = "email", unique = true)
  var emailField: String,

  @Column(name = "password")
  var passwordField: String,

  @Enumerated(EnumType.STRING)
  var role: Role = Role.USER,

  val createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
  var updatedAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),

  ) : UserDetails {
  override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
    return mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))
  }

  override fun getPassword() = passwordField

  override fun getUsername() = usernameField

  override fun isEnabled() = true
}
