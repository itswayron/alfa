package dev.weg.alfa.modules.models.item

data class ItemPatch(
    val code: String? = null,
    val description: String? = null,
    val groupId: Int? = null,
    val subgroupId: Int? = null,
    val dimensions: String? = null,
    val material: String? = null,
    val measurementUnityId: Int? = null,
    val mainSupplierId: Int? = null,
    val imagePath: String? = null,
    val isActive: Boolean? = null,
)

data class ItemResponse(
    val id: Int,
    val code: String,
    val description: String,
    val group: String,
    val subgroup: String,
    val dimensions: String? = null,
    val material: String? = null,
    val isActive: Boolean = true,
    val imagePath: String? = null,
    val measurementUnity: String,
    val mainSupplier: String? = null,
)

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
