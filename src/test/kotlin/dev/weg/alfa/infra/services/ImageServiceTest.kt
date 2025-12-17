package dev.weg.alfa.infra.services

import BaseTest
import dev.weg.alfa.modules.validators.Validator
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

class ImageServiceTest : BaseTest() {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `init - should create expected directories inside provided baseDir`() {
        val validator = mockk<Validator<MultipartFile>>()

        val baseDir = tempDir.resolve("uploads/images")
        ImageService(validator = validator, baseDir = baseDir)

        val expectedDirs = setOf("items", "profiles", "others")
        expectedDirs.forEach { dirName ->
            val dir = baseDir.resolve(dirName)
            assertTrue(dir.exists(), "Expected directory to exist: $dir")
            assertTrue(dir.isDirectory(), "Expected directory to be a directory: $dir")
        }
    }

    @Test
    fun `init - should not fail if directories already exist (idempotent initialization)`() {
        val validator = mockk<Validator<MultipartFile>>()

        val baseDir = tempDir.resolve("uploads/images")
        Files.createDirectories(baseDir.resolve("items"))
        Files.createDirectories(baseDir.resolve("profiles"))
        Files.createDirectories(baseDir.resolve("others"))

        assertDoesNotThrow {
            ImageService(validator = validator, baseDir = baseDir)
        }
        assertDoesNotThrow {
            ImageService(validator = validator, baseDir = baseDir)
        }
    }

    @Test
    fun `saveImage - should call validator, save jpg under baseDir + entityType directory, generate expected filename, and return normalized public path`() {
        val validator = mockk<Validator<MultipartFile>>()
        every { validator.validate(any()) } just Runs

        val baseDir = tempDir.resolve("uploads/images")
        val service = ImageService(validator = validator, baseDir = baseDir)

        val file = MockMultipartFile(
            "file",
            "input.png",
            "image/png",
            pngBytes(width = 2, height = 2, withAlpha = true)
        )

        val publicPath = service.saveImage(ImageService.EntityType.ITEM, "123", file)

        verify(exactly = 1) { validator.validate(file) }

        assertTrue(publicPath.startsWith("/images/"), "Expected public path to start with /images/: $publicPath")
        assertTrue(
            publicPath.matches(Regex("^/images/items/item_123_[0-9a-fA-F\\-]{36}\\.jpg$")),
            "Expected public path to include directory + filename convention: $publicPath"
        )

        val diskPath = baseDir.resolve(publicPath.removePrefix("/images/").replace("/", java.io.File.separator))
        assertTrue(diskPath.exists(), "Expected saved file to exist on disk: $diskPath")

        val saved = ImageIO.read(diskPath.toFile())
        assertNotNull(saved, "Expected saved JPG to be readable by ImageIO.read()")
        assertFalse(saved.colorModel.hasAlpha(), "Expected JPG to not have alpha channel")
    }

    @Test
    fun `saveImage - should throw when validator rejects file`() {
        val validator = mockk<Validator<MultipartFile>>()
        every { validator.validate(any()) } throws IllegalArgumentException("invalid")

        val baseDir = tempDir.resolve("uploads/images")
        val service = ImageService(validator = validator, baseDir = baseDir)

        val file = MockMultipartFile(
            "file",
            "input.png",
            "image/png",
            pngBytes(width = 1, height = 1, withAlpha = true)
        )

        assertThrows<IllegalArgumentException> {
            service.saveImage(ImageService.EntityType.USER, "u1", file)
        }

        val anyFiles = baseDir
            .resolve(ImageService.EntityType.USER.directory)
            .listDirectoryEntries()
            .any { it.name.endsWith(".jpg") }

        assertFalse(anyFiles, "Expected no jpg to be written when validation fails")
    }

    @Test
    fun `saveImage - should fail with IllegalArgumentException when ImageIO_read returns null (invalid image bytes)`() {
        val validator = mockk<Validator<MultipartFile>>()
        every { validator.validate(any()) } just Runs

        val baseDir = tempDir.resolve("uploads/images")
        val service = ImageService(validator = validator, baseDir = baseDir)

        val file = MockMultipartFile(
            "file",
            "not-an-image.bin",
            "application/octet-stream",
            byteArrayOf(0x01, 0x02, 0x03, 0x04)
        )

        assertThrows<IllegalArgumentException> {
            service.saveImage(ImageService.EntityType.OTHER, "x", file)
        }
    }

    @Test
    fun `deleteImage - when file exists should delete it (resolving from public path)`() {
        val validator = mockk<Validator<MultipartFile>>()
        val baseDir = tempDir.resolve("uploads/images")
        val service = ImageService(validator = validator, baseDir = baseDir)

        val publicPath = "/images/items/test.jpg"
        val diskPath = baseDir.resolve("items").resolve("test.jpg")
        Files.createDirectories(diskPath.parent)
        Files.write(diskPath, byteArrayOf(1, 2, 3))

        assertTrue(diskPath.exists(), "Precondition: file should exist before deletion")

        service.deleteImage(publicPath)

        assertFalse(diskPath.exists(), "Expected file to be deleted: $diskPath")
    }

    @Test
    fun `deleteImage - when file does not exist should not throw`() {
        val validator = mockk<Validator<MultipartFile>>()
        val baseDir = tempDir.resolve("uploads/images")
        val service = ImageService(validator = validator, baseDir = baseDir)

        assertDoesNotThrow {
            service.deleteImage("/images/items/does-not-exist.jpg")
        }
    }

    private fun pngBytes(width: Int, height: Int, withAlpha: Boolean): ByteArray {
        val type = if (withAlpha) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
        val img = BufferedImage(width, height, type)

        val g = img.createGraphics()
        g.color = Color(255, 0, 0, if (withAlpha) 64 else 255)
        g.fillRect(0, 0, width, height)
        g.dispose()

        val baos = ByteArrayOutputStream()
        ImageIO.write(img, "png", baos)
        return baos.toByteArray()
    }
}
