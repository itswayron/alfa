package dev.weg.alfa.modules.models.employee

import dev.weg.alfa.infra.audit.model.AuditPayload

data class EmployeeAudit(
    val id: Int,
    val name: String,
    val sectorId: Int,
) : AuditPayload

fun Employee.toAuditPayload(): EmployeeAudit =
    EmployeeAudit(
        id = this.id,
        name = this.name,
        sectorId = this.sector.id
    )
