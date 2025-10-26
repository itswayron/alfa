package dev.weg.alfa.modules.models.item

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
