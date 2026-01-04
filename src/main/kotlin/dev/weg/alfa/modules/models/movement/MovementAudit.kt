package dev.weg.alfa.modules.models.movement

import dev.weg.alfa.infra.audit.model.AuditPayload
import java.time.LocalDateTime

data class MovementAudit(
    val id: Int,
    val quantity: Double,
    val price: Double?,
    val date: LocalDateTime = LocalDateTime.now(),
    val observation: String?,

    val stockId: Int,
    val movementBatch: Int? = null,
    val employeeId: Int,
    val sectorId: Int,

    val type: MovementType,
    val status: MovementStatus,
) : AuditPayload

fun Movement.toAuditPayload(): AuditPayload =
    MovementAudit(
        id = this.id,
        quantity = this.quantity,
        price = this.price,
        date = this.date,
        observation = this.observation,
        stockId = this.stock.id,
        movementBatch = this.movementBatch?.id,
        employeeId = this.employee.id,
        sectorId = this.sector.id,
        type = this.type,
        status = this.status
    )
