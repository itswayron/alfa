package dev.weg.alfa.modules.models.stock

data class StockRequest(
    val itemId: Int,
    val minimumAmount: Double? = null,
    val maximumAmount: Double? = null,
    val sectorId: Int,
    val positionId: Int
)
