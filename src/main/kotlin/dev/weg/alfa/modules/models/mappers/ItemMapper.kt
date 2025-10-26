package dev.weg.alfa.modules.models.mappers

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.item.Item
import dev.weg.alfa.modules.models.item.ItemRequest
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Subgroup

fun ItemRequest.toEntity(
    group: Group,
    subgroup: Subgroup,
    unit: MeasurementUnity,
    supplier: BusinessPartner?,
) : Item = Item(
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
