package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.user.UserRequest
import dev.weg.alfa.modules.models.user.UserResponse
import dev.weg.alfa.security.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.USER)
class UserController(
  private val service: UserService,
) {

  @PostMapping
  fun createUser(@RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {
    val userCreated = service.createUser(userRequest)
    return ResponseEntity(userCreated, HttpStatus.CREATED)
  }

  @GetMapping("/{id}")
  fun findUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
    val response = service.findUserById(id)
    return ResponseEntity(response, HttpStatus.OK)
  }
}
