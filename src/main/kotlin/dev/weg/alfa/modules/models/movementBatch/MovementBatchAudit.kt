package dev.weg.alfa.modules.models.movementBatch

import dev.weg.alfa.infra.audit.model.AuditPayload
import java.time.LocalDateTime

data class MovementBatchAudit(
    val id: Int,
    val code: String,
    val document: String?,
    val observation: String?,
    val date: LocalDateTime,
    val businessPartnerId: Int?,
) : AuditPayload

fun MovementBatch.toAuditPayload(): AuditPayload =
    MovementBatchAudit(
        id = this.id,
        code = this.code,
        document = this.document,
        observation = this.observation,
        date = this.date,
        businessPartnerId = this.businessPartner?.id
    )
