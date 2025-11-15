package dev.weg.alfa.modules.models.stock

data class StockFilter(
    val text: String? = null,
    val groupId: Int? = null,
    val subgroupId: Int? = null,
    val supplierId: Int? = null,
    val isActive: Boolean? = null
)
