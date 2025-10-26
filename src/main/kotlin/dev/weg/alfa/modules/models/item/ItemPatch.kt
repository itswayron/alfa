package dev.weg.alfa.modules.models.item

data class ItemPatch(
    val code: String? = null,
    val description: String? = null,
    val groupId: Int? = null,
    val subgroupId: Int? = null,
    val dimensions: String? = null,
    val material: String? = null,
    val measurementUnityId: Int? = null,
    val mainSupplier: Int? = null,
)