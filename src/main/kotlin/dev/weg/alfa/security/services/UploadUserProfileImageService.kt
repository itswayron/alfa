package dev.weg.alfa.security.services

import dev.weg.alfa.infra.services.ImageService
import dev.weg.alfa.modules.models.user.User
import dev.weg.alfa.modules.repositories.user.UserRepository
import dev.weg.alfa.modules.repositories.utils.getCurrentUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class UploadUserProfileImageService(
    private val repository: UserRepository,
    private val imageService: ImageService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(imageFile: MultipartFile) {
        val user = repository.getCurrentUser()
        logger.info("Uploading profile image for user: ${user.username}")

        user.deleteImageIfExists()
        val imagePath = imageService.saveImage("user", user.id.toString(), imageFile)
        logger.debug("Saving new profile image for userId='{}'.", user.id)

        user.profileImagePath = imagePath
        user.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

        repository.save(user)
        logger.info("Profile image updated successfully for user='{}'. New path='{}'.", user.username, imagePath)
    }


    fun deleteProfilePhoto() {
        val user = repository.getCurrentUser()
        logger.info("Deleting profile photo for user ID='{}'", user.id)
        user.deleteImageIfExists()
    }

    private fun User.deleteImageIfExists() {
        this.profileImagePath?.let { path ->
            try {
                logger.debug("Attempting to delete existing profile image at path='{}'.", path)
                imageService.deleteImage(path)
                this.profileImagePath = null
                repository.save(this)
                logger.info("Previous profile image deleted for user='{}'.", this.username)
            } catch (ex: Exception) {
                logger.warn(
                    "Could not delete old profile image for user='{}'. Path='{}'. Reason: {}",
                    this.username,
                    path,
                    ex.message
                )
            }
        } ?: logger.debug("No existing profile image to delete for user='{}'.", this.username)
    }
}
