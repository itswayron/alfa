package dev.weg.alfa.modules.models.productionOrder

import java.time.LocalDateTime

data class ProductionOrderPatch(
    val code: String? = null,
    val document: String? = null,
    val date: LocalDateTime? = null,
    val mainSupplierId: Int? = null
)
