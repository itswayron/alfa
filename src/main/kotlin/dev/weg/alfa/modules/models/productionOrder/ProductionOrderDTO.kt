package dev.weg.alfa.modules.models.productionOrder

import java.time.LocalDateTime

data class ProductionOrderPatch(
    val code: String? = null,
    val document: String? = null,
    val date: LocalDateTime? = null,
    val mainSupplierId: Int? = null
)

data class ProductionOrderRequest(
    val code: String,
    val document: String?,
    val mainSupplierId: Int,
    val date: LocalDateTime,
)

data class ProductionOrderResponse(
    val id: Int,
    val code: String,
    val document: String?,
    val date: LocalDateTime,
    val businessPartner: String,
)
