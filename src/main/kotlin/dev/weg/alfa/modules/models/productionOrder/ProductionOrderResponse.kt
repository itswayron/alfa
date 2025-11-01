package dev.weg.alfa.modules.models.productionOrder

import java.time.LocalDateTime

data class ProductionOrderResponse(
    val id: Int,
    val code: String,
    val document: String?,
    val date: LocalDateTime,
    val businessPartner: String,
)
