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
import java.util.UUID
import javax.imageio.ImageIO

@Service
class ImageService(
    private val validator: Validator<MultipartFile>,
    private val baseDir: Path = Paths.get("uploads/images"),
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        logger.info("Initializing ImageService and creating necessary directories.")
        Files.createDirectories(baseDir.resolve("items"))
        Files.createDirectories(baseDir.resolve("profiles"))
        logger.debug("Image directories initialized at: {}", baseDir.toAbsolutePath())
    }

    fun saveImage(entityType: String, entityId: String, file: MultipartFile): String {
        logger.info("Saving image for entityType='{}' with entityId='{}'.", entityType, entityId)
        validator.validate(file)
        val filename = "${entityType}_${entityId}_${UUID.randomUUID()}.jpg"

        val subfolder = when (entityType.lowercase()) {
            // Todo - use a better structure if the types of images grows
            "item" -> "items"
            "user" -> "profiles"
            else -> {
                logger.error("Unknown entityType='{}'. Aborting image save.", entityType)
                throw IllegalArgumentException("Unsupported entityType: $entityType")
            }
        }

        val folderPath = baseDir.resolve(subfolder)
        val filePath = folderPath.resolve(filename)

        logger.trace("Saving image to path: {}", filePath.toAbsolutePath())

        convertAndSaveAsJpg(file, filePath)
        logger.info("Image saved successfully at path='{}'.", filePath)

        return normalizePath(filePath.toString())
    }

    fun deleteImage(path: String) {
        logger.info("Deleting image at relative path='{}'.", path)
        val relativePath = path.removePrefix("/images/").replace("/", FileSystems.getDefault().separator)
        val imagePath = baseDir.resolve(relativePath)

        if (Files.exists(imagePath)) {
            Files.delete(imagePath)
            logger.info("Image successfully deleted from disk: {}", imagePath.toAbsolutePath())
        } else {
            logger.warn("Image not found at path='{}'. Nothing to delete.", imagePath.toAbsolutePath())
        }
    }

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

    private fun normalizePath(physicalPath: String): String {
        val relativePath = baseDir.relativize(Paths.get(physicalPath)).toString().replace("\\", "/")
        val publicPath = "/images/$relativePath"
        logger.trace("Normalized path: '{}' -> '{}'", physicalPath, publicPath)
        return "/images/$relativePath"
    }
}
