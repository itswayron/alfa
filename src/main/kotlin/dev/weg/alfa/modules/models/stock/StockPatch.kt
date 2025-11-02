package dev.weg.alfa.modules.models.stock

data class StockPatch(
    val minimumAmount: Double? = null,
    val maximumAmount: Double? = null,
    val sectorId: Int? = null,
    val positionId: Int? = null
)
