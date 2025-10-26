package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.item.Item
import dev.weg.alfa.modules.models.item.ItemRequest
import dev.weg.alfa.modules.models.mappers.toEntity
import dev.weg.alfa.modules.repositories.businessPartner.BusinessPartnerRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.item.ItemRepository
import dev.weg.alfa.modules.repositories.simpleEntities.GroupRepository
import dev.weg.alfa.modules.repositories.simpleEntities.MeasurementUnityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val repository: ItemRepository,
    private val groupRepository: GroupRepository,
    private val subgroupRepository: GroupRepository,
    private val unitRepository: MeasurementUnityRepository,
    private val partnerRepository: BusinessPartnerRepository,
    //private val validator: Validator<Item>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createItem(request: ItemRequest): Item {
        val sanitizedRequest = request.sanitized()
        logger.info("Creating item: ${sanitizedRequest.description}")

        //validator.valitade(sanitizedRequest)
        val group = groupRepository.findByIdOrThrow(request.groupId)
        val subGroup = subgroupRepository.findByIdOrThrow(request.subgroupId)
        val unit = unitRepository.findByIdOrThrow(request.measurementUnityId)
        val supplier = if(request.mainSupplierId != null) {
            partnerRepository.findByIdOrThrow(request.mainSupplierId)
        } else {
            null
        }

        val newItem = request.toEntity(
            group = group,
            subgroup = group,
            unit = unit,
            supplier = supplier
        )

        logger.info("Item: ${newItem.description} created with id; ${newItem.id}")
        return newItem
    }

    private fun ItemRequest.sanitized(): ItemRequest =
        ItemRequest(
            code = this.code.trim(),
            description = this.description.trim(),
            groupId = this.groupId,
            subgroupId = this.subgroupId,
            dimensions = this.dimensions?.trim(),
            material = this.material?.trim(),
            measurementUnityId = this.measurementUnityId,
            mainSupplierId = this.mainSupplierId
        )

}