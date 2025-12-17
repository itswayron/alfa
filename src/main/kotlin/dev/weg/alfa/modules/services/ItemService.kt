package dev.weg.alfa.modules.services

import dev.weg.alfa.infra.services.ImageService
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.item.*
import dev.weg.alfa.modules.repositories.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.ItemRepository
import dev.weg.alfa.modules.repositories.simpleEntities.GroupRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MeasurementUnityRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.findByIdIfNotNull
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

// TODO: Unit Test : Should sanitize ItemRequest fields before creation
// TODO: Unit Test : Should throw when groupId is invalid during item creation
// TODO: Unit Test : Should throw when subgroupId is invalid during item creation
// TODO: Unit Test : Should throw when measurementUnityId is invalid during item creation
// TODO: Unit Test : Should create a new item successfully when all dependencies are valid

// TODO: Unit Test : Should retrieve item by ID and map to response correctly

// TODO: Unit Test : Should map Page<Item> to PageDTO<ItemResponse> in getItems

// TODO: Unit Test : Should apply partial update preserving unspecified fields
// TODO: Unit Test : Should apply partial update replacing only provided fields
// TODO: Unit Test : Should throw when patch references invalid group/subgroup/unit/supplier
// TODO: Unit Test : Should save patched item with correct final state

// TODO: Unit Test : Should delete item with existing image and trigger image deletion
// TODO: Unit Test : Should delete item without image without calling image service

// TODO: Unit Test : Should upload new image after deleting previous one
// TODO: Unit Test : Should upload image for item without previous image

// TODO: Unit Test : Should delete only the image when calling deleteImage() (not the item)

// TODO: Integration Test : Should create an item end-to-end with DB and return correct response
// TODO: Integration Test : Should update item partially and persist changes correctly
// TODO: Integration Test : Should return paginated items correctly
// TODO: Integration Test : Should delete item from database
@Service
class ItemService(
    private val repository: ItemRepository,
    private val groupRepository: GroupRepository,
    private val subgroupRepository: SubgroupRepository,
    private val unitRepository: MeasurementUnityRepository,
    private val partnerRepository: BusinessPartnerRepository,
    private val imageService: ImageService,
    //private val validator: Validator<Item>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createItem(request: ItemRequest): ItemResponse {
        val sanitizedRequest = request.sanitized()
        logger.info("Creating item: ${sanitizedRequest.description}")

        //validator.validate(sanitizedRequest)
        val group = groupRepository.findByIdOrThrow(request.groupId)
        val subGroup = subgroupRepository.findByIdOrThrow(request.subgroupId)
        val unit = unitRepository.findByIdOrThrow(request.measurementUnityId)
        val supplier = partnerRepository.findByIdIfNotNull(request.mainSupplierId)

        val newItem = sanitizedRequest.toEntity(
            group = group, subgroup = subGroup, unit = unit, supplier = supplier
        )

        val entity = repository.save(newItem)
        logger.info("Item: ${entity.description} created with id; ${entity.id}")

        val response = entity.toResponse()
        return response
    }

    fun getItemById(id: Int): ItemResponse {
        logger.info("Fetching item with ID: $id")

        val item = repository.findByIdOrThrow(id)
        logger.info("Retrieved item with ID: $id name: ${item.description}")

        val response = item.toResponse()
        return response
    }

    fun getItems(pageable: Pageable): PageDTO<ItemResponse> {
        logger.info("Fetching items from the repository.")

        val page = repository.findAll(pageable)
        logger.info("Retrieved {} items.", page.content.size)

        val response = page.map { it.toResponse() }.toDTO()
        return response
    }

    fun updateItem(command: Pair<Int, ItemPatch>): ItemResponse {
        val (id, itemUpdated) = command
        logger.info("Updating item ID={} with patch: {}", id, itemUpdated)

        val oldItem = repository.findByIdOrThrow(id)
        logger.debug("Original item data: {}", oldItem)

        val group = groupRepository.findByIdIfNotNull(itemUpdated.groupId)
        val subgroup = subgroupRepository.findByIdIfNotNull(itemUpdated.subgroupId)
        val unit = unitRepository.findByIdIfNotNull(itemUpdated.measurementUnityId)
        val supplier = partnerRepository.findByIdIfNotNull(itemUpdated.mainSupplierId)

        val newItem = oldItem.applyPatch(
            patch = itemUpdated, group = group, subgroup = subgroup, unit = unit, supplier = supplier
        )

        // validator.validate(newItem)

        val response = repository.save(newItem).toResponse()
        logger.info("Item updated: ID={}, description='{}'", response.id, response.description)

        return response
    }

    fun deleteItem(id: Int) {
        logger.info("Deleting item with ID='{}'", id)
        val deletedItem = repository.findByIdOrThrow(id)
        logger.info("Item to delete found ID='{}', description='{}'", deletedItem.id, deletedItem.description)

        deletedItem.deleteImageIfExists()
        repository.deleteById(deletedItem.id)
        logger.info("Item deleted successfully. ID='{}'", id)
    }

    fun uploadItemImage(id: Int, imageFile: MultipartFile) {
        logger.info("Upload item photo for item ID={}", id)
        val item = repository.findByIdOrThrow(id)
        item.deleteImageIfExists()

        val imagePath = imageService.saveImage(ImageService.EntityType.ITEM, item.id.toString(), imageFile)
        item.imagePath = imagePath

        repository.save(item)
        logger.info("Item image updated successfully for item ID={}", item.id)
    }

    fun deleteImage(id: Int) {
        logger.info("Deleting image of Item ID='{}'", id)
        val item = repository.findByIdOrThrow(id)
        item.deleteImageIfExists()
    }

    private fun ItemRequest.sanitized(): ItemRequest = ItemRequest(
        code = this.code.trim(),
        description = this.description.trim(),
        groupId = this.groupId,
        subgroupId = this.subgroupId,
        dimensions = this.dimensions?.trim(),
        material = this.material?.trim(),
        measurementUnityId = this.measurementUnityId,
        mainSupplierId = this.mainSupplierId
    )

    private fun Item.deleteImageIfExists() {
        this.imagePath?.let { path ->
            try {
                logger.debug("Attempting to delete existing item image at path={}", path)
                imageService.deleteImage(path)
                this.imagePath = null
                repository.save(this)
                logger.info("Item image deleted successfully. Path={}", path)
            } catch (ex: Exception) {
                logger.warn("Failed to delete item image. Path={}, Reason={}", path, ex.message)
            }
        } ?: logger.debug("No existing item image to delete for book ID={}", this.id)
    }
}
