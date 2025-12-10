package dev.weg.alfa.infra.services

import dev.weg.alfa.modules.validators.Validator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageService(
    private val validator: Validator<MultipartFile>,
    private val baseDir: Path = Paths.get("uploads/images"),
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    enum class EntityType(
        val directory: String,
    ) {
        ITEM("items"),
        USER("profiles"),
        OTHER("others");
    }

    // TODO: Test: Should create the expected directories inside the provided baseDir.
    // TODO: Test: Should NOT fail if the directories already exist (idempotent initialization).
    init {
        logger.info("Initializing ImageService and creating necessary directories.")
        EntityType.entries.forEach { type ->
            val dir = baseDir.resolve(type.directory)
            Files.createDirectories(dir)
            logger.debug("Created directory for '{}' at '{}'.", type.name, dir.toAbsolutePath())
        }
        logger.debug("Image directories initialized at: '{}'", baseDir.toAbsolutePath())
    }

    // TODO: Test: Save a valid image using baseDir + entityType.directory
    // TODO: Test: Generate a filename with entityType.name.lowercase() + entityId + UUID
    // TODO: Test: Call validator.validate
    // TODO: Test: Throw exception when image is not valid
    // TODO: Test: Should return a normalized public path starting with "/images/".
    fun saveImage(entityType: EntityType, entityId: String, file: MultipartFile): String {
        logger.info("Saving image for entityType='{}' with entityId='{}'.", entityType, entityId)
        validator.validate(file)
        val filename = "${entityType.name.lowercase()}_${entityId}_${UUID.randomUUID()}.jpg"

        val folderPath = baseDir.resolve(entityType.directory)
        val filePath = folderPath.resolve(filename)
        logger.trace("Saving image to path: '{}'", filePath.toAbsolutePath())

        convertAndSaveAsJpg(file, filePath)
        logger.info("Image saved successfully at path='{}'.", filePath)

        return normalizePath(filePath.toString())
    }

    // TODO: Test: When the file exists.
    // TODO: Test: When the file/path doesn't exist.
    // TODO: Test: No exceptions when the file doesn't exists, just logging.
    // TODO: Test: Resolving correctly the public path in `baseDir.resolve`
    fun deleteImage(path: String) {
        logger.info("Deleting image at relative path='{}'.", path)
        val relativePath = path.removePrefix("/images/").replace("/", FileSystems.getDefault().separator)
        val imagePath = baseDir.resolve(relativePath)

        if (Files.exists(imagePath)) {
            Files.delete(imagePath)
            logger.info("Image successfully deleted from disk: '{}'", imagePath.toAbsolutePath())
        } else {
            logger.warn("Image not found at path='{}'. Nothing to delete.", imagePath.toAbsolutePath())
        }
    }

    // TODO: Test: Convert an PNG/JPG image and save as .JPG
    // TODO: Test: Saved file must be readable by ImageIO.read()
    // TODO: Test: The image must not have the alpha
    // TODO: Test: Should fail with IllegalArgumentException when ImageIO.Read throws null
    private fun convertAndSaveAsJpg(file: MultipartFile, targetPath: Path) {
        logger.trace("Converting file to JPG at path='{}'.", targetPath.toAbsolutePath())
        val originalImage = ImageIO.read(file.inputStream) ?: throw IllegalArgumentException("Invalid image file.")

        val rgbImage = BufferedImage(originalImage.width, originalImage.height, BufferedImage.TYPE_INT_RGB)
        val g = rgbImage.createGraphics()
        g.drawImage(originalImage, 0, 0, java.awt.Color.WHITE, null)
        g.dispose()

        val outputFile = targetPath.toFile()
        val success = ImageIO.write(rgbImage, "jpg", outputFile)

        if (!success) {
            logger.error("No suitable ImageWriter found for format 'jpg'. Image not saved.")
        }

        logger.debug("Image converted and written to disk at '{}'.", targetPath.toAbsolutePath())
    }

    // TODO: Create tests with multiple types of paths
    private fun normalizePath(physicalPath: String): String {
        val relativePath = baseDir.relativize(Paths.get(physicalPath)).toString().replace("\\", "/")
        val publicPath = "/images/$relativePath"
        logger.trace("Normalized path: '{}' -> '{}'", physicalPath, publicPath)
        return "/images/$relativePath"
    }
}
