package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.item.ItemPatch
import dev.weg.alfa.modules.models.item.ItemRequest
import dev.weg.alfa.modules.models.item.ItemResponse
import dev.weg.alfa.modules.models.mappers.toDTO
import dev.weg.alfa.modules.models.mappers.toEntity
import dev.weg.alfa.modules.models.mappers.toResponse
import dev.weg.alfa.modules.models.mappers.applyPatch
import dev.weg.alfa.modules.repositories.businessPartner.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.item.ItemRepository
import dev.weg.alfa.modules.repositories.simpleEntities.GroupRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MeasurementUnityRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val repository: ItemRepository,
    private val groupRepository: GroupRepository,
    private val subgroupRepository: SubgroupRepository,
    private val unitRepository: MeasurementUnityRepository,
    private val partnerRepository: BusinessPartnerRepository,
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

        val newItem = request.toEntity(
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

        repository.deleteById(deletedItem.id)
        logger.info("Item deleted successfully. ID='{}'", id)
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

    private inline fun <reified T : Any, ID : Any> JpaRepository<T, ID>.findByIdIfNotNull(id: ID?): T? =
        id?.let { findByIdOrThrow(it) }
}
