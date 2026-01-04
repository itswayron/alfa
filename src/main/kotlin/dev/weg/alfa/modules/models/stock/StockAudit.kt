package dev.weg.alfa.modules.models.stock

import dev.weg.alfa.infra.audit.model.AuditPayload

data class StockAudit(
    val id: Int,
    val itemId: Int,
    val currentAmount: Double,
    val minimumAmount: Double?,
    val maximumAmount: Double?,
    val averagePrice: Double,
    val sectorId: Int,
    val positionId: Int,
) : AuditPayload

fun Stock.toAuditPayload(): StockAudit =
    StockAudit(
        id = this.id,
        itemId = this.item.id,
        currentAmount = this.currentAmount,
        minimumAmount = this.minimumAmount,
        maximumAmount = this.maximumAmount,
        averagePrice = this.averagePrice,
        sectorId = this.sector.id,
        positionId = this.position.id,
    )
