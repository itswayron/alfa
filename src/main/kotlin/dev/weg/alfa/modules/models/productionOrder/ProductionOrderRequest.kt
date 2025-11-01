package dev.weg.alfa.modules.models.productionOrder

import java.time.LocalDateTime

data class ProductionOrderRequest(
    val code: String,
    val document: String?,
    val mainSupplierId: Int,
    val date: LocalDateTime
)
