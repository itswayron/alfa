package dev.weg.alfa.modules.models.item

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Subgroup

fun ItemRequest.toEntity(
    group: Group,
    subgroup: Subgroup,
    unit: MeasurementUnity,
    supplier: BusinessPartner?,
): Item = Item(
    id = 0,
    code = this.code,
    description = this.description,
    group = group,
    subgroup = subgroup,
    dimensions = this.dimensions,
    material = this.material,
    isActive = true,
    imagePath = null,
    measurementUnity = unit,
    mainSupplier = supplier
)

fun Item.toResponse(): ItemResponse = ItemResponse(
    id = this.id,
    code = this.code,
    description = this.description,
    group = this.group.name,
    subgroup = this.subgroup.name,
    dimensions = this.dimensions,
    material = this.material,
    isActive = this.isActive,
    imagePath = this.imagePath,
    measurementUnity = this.measurementUnity.name,
    mainSupplier = this.mainSupplier?.name,
)

fun Item.applyPatch(
    patch: ItemPatch,
    group: Group?,
    subgroup: Subgroup?,
    unit: MeasurementUnity?,
    supplier: BusinessPartner?
): Item =
    Item(
        id = this.id,
        code = patch.code ?: this.code,
        description = patch.description ?: this.description,
        group = group ?: this.group,
        subgroup = subgroup ?: this.subgroup,
        dimensions = patch.dimensions ?: this.dimensions,
        material = patch.material ?: this.material,
        isActive = patch.isActive ?: this.isActive,
        imagePath = patch.imagePath ?: this.imagePath,
        measurementUnity = unit ?: this.measurementUnity,
        mainSupplier = supplier ?: this.mainSupplier,
    )
