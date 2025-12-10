package dev.weg.alfa.infra.validators

import dev.weg.alfa.infra.exceptions.ImageNotValidException
import dev.weg.alfa.modules.validators.ValidationErrorMessages
import dev.weg.alfa.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import javax.imageio.ImageIO

@Component
class ImageValidator : Validator<MultipartFile> {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val maxSizeBytes = 5 * 1024 * 1024

    init {
        ImageIO.scanForPlugins()
    }

    // TODO: Test: Should throw ImageNotValidException when file is empty.
    // TODO: Test: Should throw ImageNotValidException when contentType is not an image/*.
    // TODO: Test: Should throw ImageNotValidException when file size exceeds maxSizeBytes.
    // TODO: Test: Should throw ImageNotValidException when ImageIO.read() returns null (unsupported format).
    // TODO: Test: Should throw ImageNotValidException when image dimensions are below minimum (200x200).
    // TODO: Test: Should NOT throw when file is valid and meets all constraints.
    // TODO: Test: Should accumulate multiple error messages when more than one validation fails.
    // TODO: Test: Should call ImageIO.read() exactly once in validateImageDimensions().
    override fun validate(t: MultipartFile) {
        logger.debug("Validating image: ${t.name}")
        val errors = mutableListOf<String>()
        validateFileExists(t, errors)
        validateIsImage(t, errors)
        validateFileSize(t, errors)
        validateImageDimensions(t, errors)

        if (errors.isNotEmpty()) {
            logger.error("Image is not valid")
            throw ImageNotValidException(errors)
        }

        logger.debug("Valid image.")
    }

    private fun validateFileExists(file: MultipartFile, errors: MutableList<String>) {
        logger.debug("Validating if files exists.")
        if (file.isEmpty) {
            logger.error("File does not exists.")
            errors.add(ValidationErrorMessages.IMAGE_EMPTY.message)
        } else {
            logger.debug("File exists.")
        }
    }

    private fun validateIsImage(file: MultipartFile, errors: MutableList<String>) {
        logger.debug("Validating if file is image.")
        if (file.contentType?.startsWith("image/") != true) {
            logger.error("File is not an image.")
            errors.add(ValidationErrorMessages.NOT_IMAGE.message)
        } else {
            logger.debug("Valid file type.")
        }
    }

    private fun validateFileSize(file: MultipartFile, errors: MutableList<String>) {
        logger.debug("Validating file size.")
        if (file.size > maxSizeBytes) {
            logger.error("File is too big")
            errors.add(ValidationErrorMessages.BIG_FILE.message)
        } else {
            logger.debug("Valid file size.")
        }
    }

    private fun validateImageDimensions(file: MultipartFile, errors: MutableList<String>) {
        logger.debug("Validating image dimensions")
        val image = ImageIO.read(file.inputStream)
        if (image == null) {
            logger.error("Unsupported image format or unable to read image.")
            errors.add(ValidationErrorMessages.UNSUPPORTED_IMAGE.message)
            return
        }
        if (image.width < 200 || image.height < 200) {
            logger.error("Image dimensions are too small.")
            errors.add(ValidationErrorMessages.SMALL_DIMENSIONS.message)
        } else {
            logger.debug("Valid image dimensions.")
        }
    }
}
