package dev.weg.alfa.modules.models.item

import dev.weg.alfa.infra.audit.model.AuditPayload

data class ItemAudit(
    val id: Int,
    val code: String,
    val description: String,

    val groupId: Int,
    val subgroupId: Int,

    val dimensions: String? = null,
    val material: String? = null,
    val isActive: Boolean = true,
    var imagePath: String? = null,

    val measurementUnityId: Int,
    val mainSupplierId: Int? = null,
) : AuditPayload

fun Item.toAuditPayload(): AuditPayload =
    ItemAudit(
        id = this.id,
        code = this.code,
        description = this.description,
        groupId = this.group.id,
        subgroupId = this.subgroup.id,
        dimensions = this.dimensions,
        material = this.material,
        isActive = this.isActive,
        imagePath = this.imagePath,
        measurementUnityId = this.measurementUnity.id,
        mainSupplierId = this.mainSupplier?.id
    )
