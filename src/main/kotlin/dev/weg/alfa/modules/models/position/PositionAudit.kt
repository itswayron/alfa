package dev.weg.alfa.modules.models.position

import dev.weg.alfa.infra.audit.model.AuditPayload

data class PositionAudit(
    val id: Int,
    val floor: String,
    val side: String,
    val column: String,
    val box: String,
) : AuditPayload

fun Position.toAuditPayload(): PositionAudit =
    PositionAudit(
        id = this.id,
        floor = this.floor,
        side = this.side,
        column = this.column,
        box = this.box,
    )
