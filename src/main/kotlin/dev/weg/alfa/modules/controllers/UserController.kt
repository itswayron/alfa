package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.user.UserRequest
import dev.weg.alfa.modules.models.user.UserResponse
import dev.weg.alfa.security.services.UploadUserProfileImageService
import dev.weg.alfa.security.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(ApiRoutes.USER)
class UserController(
    private val service: UserService,
    private val uploadUserProfileImageService: UploadUserProfileImageService,
) {
    @PostMapping
    fun createUser(@RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {
        val userCreated = service.createUser(userRequest)
        return ResponseEntity(userCreated, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun findUserById(@PathVariable id: Int): ResponseEntity<UserResponse> {
        val response = service.findUserById(id)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val response = service.findCurrentUser()
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/profile", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfileImage(@RequestPart("profileImage") profileImageFile: MultipartFile): ResponseEntity<Unit> {
        uploadUserProfileImageService.execute(profileImageFile)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @DeleteMapping("/profile/image")
    fun deleteProfilePhoto(): ResponseEntity<Unit> {
        uploadUserProfileImageService.deleteProfilePhoto()
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
