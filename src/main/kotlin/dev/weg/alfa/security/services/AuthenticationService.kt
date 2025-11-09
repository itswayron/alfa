package dev.weg.alfa.security.services

import dev.weg.alfa.security.config.JwtProperties
import dev.weg.alfa.security.config.SecurityLogger
import dev.weg.alfa.security.models.AuthenticationRequest
import dev.weg.alfa.security.models.AuthenticationResponse
import dev.weg.alfa.utils.maskLast
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
) {

    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
        val username = authRequest.username
        SecurityLogger.log.info("Authentication attempt for user='{}'", username)

        val authentication: Authentication = try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(username, authRequest.password)
            )
        } catch (ex: BadCredentialsException) {
            SecurityLogger.log.warn("Authentication failed for user='{}': invalid credentials", username)
            throw ex
        } catch (ex: Exception) {
            SecurityLogger.log.error("Unexpected error during authentication for user='{}': {}", username, ex.message)
            throw ex
        }

        val user = authentication.principal as UserDetails
        SecurityLogger.log.debug("User '{}' successfully authenticated via AuthenticationManager", username)

        val accessToken = generateAccessToken(user)

        SecurityLogger.log.info(
            "Access token generated for user='{}'. Token preview='{}'",
            username,
            accessToken.maskLast(6)
        )

        return AuthenticationResponse(accessToken)
    }

    private fun generateAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
    )
}
