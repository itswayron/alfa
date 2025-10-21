package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import java.time.LocalDateTime

data class ProductionOrder(
    val id: Int,
    val code: String,
    val document: String?,
    val date: LocalDateTime,
    val businessPartner: BusinessPartner,
)
