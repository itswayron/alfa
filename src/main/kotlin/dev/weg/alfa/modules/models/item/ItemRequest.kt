package dev.weg.alfa.modules.models.item

class ItemRequest(
    val code: String,
    val description: String,
    val groupId: Int,
    val subgroupId: Int,
    val dimensions: String? = null,
    val material: String? = null,
    val measurementUnityId: Int,
    val mainSupplierId: Int? = null,
)
