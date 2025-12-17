package dev.weg.alfa.infra.validators

import BaseTest
import dev.weg.alfa.infra.exceptions.ImageNotValidException
import dev.weg.alfa.modules.validators.ValidationErrorMessages
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

class ImageValidatorTest : BaseTest() {

    @Test
    fun `should throw ImageNotValidException when file is empty`() {
        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "empty.png",
            "image/png",
            byteArrayOf()
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }
        assertTrue(ex.errors.contains(ValidationErrorMessages.IMAGE_EMPTY.message))
    }

    @Test
    fun `should throw ImageNotValidException when contentType is image but bytes are not a readable image (unsupported)`() {
        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "x.png",
            "image/png",
            byteArrayOf(0x01, 0x02, 0x03, 0x04)
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }
        assertTrue(ex.errors.contains(ValidationErrorMessages.UNSUPPORTED_IMAGE.message))
    }

    @Test
    fun `should throw ImageNotValidException when contentType is not image`() {
        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "file.txt",
            "text/plain",
            "hello".toByteArray()
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }
        assertTrue(ex.errors.contains(ValidationErrorMessages.NOT_IMAGE.message))
    }

    @Test
    fun `should throw ImageNotValidException when file size exceeds maxSizeBytes`() {
        val validator = ImageValidator()

        val tooBig = ByteArray(5 * 1024 * 1024 + 1) { 0 } // > 5MB
        val file = MockMultipartFile(
            "file",
            "big.png",
            "image/png",
            tooBig
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }
        assertTrue(ex.errors.contains(ValidationErrorMessages.BIG_FILE.message))
    }

    @Test
    fun `should throw ImageNotValidException when ImageIO read returns null (unsupported format)`() {
        mockkStatic(ImageIO::class)
        every { ImageIO.scanForPlugins() } just Runs
        every { ImageIO.read(any<InputStream>()) } returns null

        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "file.webp",
            "image/webp",
            byteArrayOf(0x01, 0x02, 0x03)
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }
        assertTrue(ex.errors.contains(ValidationErrorMessages.UNSUPPORTED_IMAGE.message))

        unmockkStatic(ImageIO::class)
    }

    @Test
    fun `should throw ImageNotValidException when image dimensions are below minimum`() {
        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "small.png",
            "image/png",
            pngBytes(width = 199, height = 200)
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }
        assertTrue(ex.errors.contains(ValidationErrorMessages.SMALL_DIMENSIONS.message))
    }

    @Test
    fun `should not throw when file is valid and meets all constraints`() {
        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "ok.png",
            "image/png",
            pngBytes(width = 200, height = 200)
        )

        assertDoesNotThrow {
            validator.validate(file)
        }
    }

    @Test
    fun `should accumulate multiple error messages when more than one validation fails`() {
        val validator = ImageValidator()

        val file = MockMultipartFile(
            "file",
            "empty.txt",
            "text/plain",
            byteArrayOf()
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }

        assertTrue(ex.errors.size >= 2, "Expected at least 2 validation errors, but got: ${ex.errors}")
        assertTrue(ex.errors.contains(ValidationErrorMessages.IMAGE_EMPTY.message))
        assertTrue(ex.errors.contains(ValidationErrorMessages.NOT_IMAGE.message))
    }

    @Test
    fun `should accumulate multiple error messages when content type is not image and file is too big`() {
        val validator = ImageValidator()

        val tooBig = ByteArray(5 * 1024 * 1024 + 1) { 0 } // > 5MB
        val file = MockMultipartFile(
            "file",
            "big.txt",
            "text/plain",
            tooBig
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }

        val expected = setOf(
            ValidationErrorMessages.NOT_IMAGE.message,
            ValidationErrorMessages.BIG_FILE.message
        )
        assertTrue(ex.errors.containsAll(expected), "Expected errors to contain $expected but got: ${ex.errors}")
    }

    @Test
    fun `should accumulate multiple error messages when image is unreadable and file is too big`() {
        mockkStatic(ImageIO::class)
        every { ImageIO.scanForPlugins() } just Runs
        every { ImageIO.read(any<InputStream>()) } returns null

        val validator = ImageValidator()

        val tooBig = ByteArray(5 * 1024 * 1024 + 1) { 0 }
        val file = MockMultipartFile(
            "file",
            "big-invalid.png",
            "image/png",
            tooBig
        )

        val ex = assertThrows<ImageNotValidException> { validator.validate(file) }

        val expected = setOf(
            ValidationErrorMessages.BIG_FILE.message,
            ValidationErrorMessages.UNSUPPORTED_IMAGE.message
        )
        assertTrue(ex.errors.containsAll(expected), "Expected errors to contain $expected but got: ${ex.errors}")

        unmockkStatic(ImageIO::class)
    }

    @Test
    fun `should call ImageIO read exactly once in validateImageDimensions`() {
        val bytes = pngBytes(width = 200, height = 200)

        mockkStatic(ImageIO::class)
        try {
            every { ImageIO.scanForPlugins() } just Runs
            every { ImageIO.read(any<InputStream>()) } returns BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)

            val validator = ImageValidator()

            val file = MockMultipartFile(
                "file",
                "ok.png",
                "image/png",
                bytes
            )

            assertDoesNotThrow { validator.validate(file) }

            verify(exactly = 1) { ImageIO.read(any<InputStream>()) }
        } finally {
            unmockkStatic(ImageIO::class)
        }
    }

    private fun pngBytes(width: Int, height: Int): ByteArray {
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val baos = ByteArrayOutputStream()
        ImageIO.write(img, "png", baos)
        return baos.toByteArray()
    }
}
