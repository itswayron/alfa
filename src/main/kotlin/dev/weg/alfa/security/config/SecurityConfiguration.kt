package dev.weg.alfa.security.config

import dev.weg.alfa.config.ApiRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(private val authenticationProvider: AuthenticationProvider) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors {}
            .authorizeHttpRequests { registry ->
                registry.requestMatchers("${ApiRoutes.AUTH}/status").authenticated()
                registry
                    .requestMatchers("${ApiRoutes.AUTH}/**").permitAll()
                    .requestMatchers("${ApiRoutes.USER}/**").permitAll()
                    .requestMatchers("/error").permitAll()
                registry.requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                registry.requestMatchers(HttpMethod.GET, "/images/**").permitAll()

                listOf(
                    "${ApiRoutes.GROUP}/**",
                    "${ApiRoutes.PARTNER}/**",
                    "${ApiRoutes.MEASURAMENT_UNITS}/**",
                    "${ApiRoutes.SUBGROUP}/**",
                    "${ApiRoutes.MOVEMENT_STATUS}/**",
                    "${ApiRoutes.MOVEMENT_TYPES}/**",
                    "${ApiRoutes.SECTOR}/**",
                ).forEach {
                    registry.requestMatchers(HttpMethod.GET, it).authenticated()
                    registry.requestMatchers(it).authenticated()
                }

                registry.anyRequest().fullyAuthenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173", "http://localhost:5174", "http://localhost:4000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
