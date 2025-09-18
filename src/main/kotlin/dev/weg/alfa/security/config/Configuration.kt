package dev.weg.alfa.security.config

import dev.weg.alfa.modules.models.user.UserRequest
import dev.weg.alfa.modules.repositories.UserRepository
import dev.weg.alfa.modules.validators.Validator
import dev.weg.alfa.security.services.UserService
import dev.weg.alfa.security.validators.UserPersistenceValidator
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration {

  @Bean
  fun userService(
    userRepository: UserRepository,
    encoder: PasswordEncoder,
    validator: Validator<UserRequest>,
    persistenceValidator: UserPersistenceValidator,
    passwordValidator: Validator<String>,
  ) =
    UserService(userRepository, encoder, validator, persistenceValidator, passwordValidator)

  @Bean
  fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun authenticationProvider(
    userService: UserService,
    encoder: PasswordEncoder
  ): AuthenticationProvider =
    DaoAuthenticationProvider(userService).apply {
      setPasswordEncoder(encoder)
    }

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager
}
