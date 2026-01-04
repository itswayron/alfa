package dev.weg.alfa.modules.models.lending

import dev.weg.alfa.infra.audit.model.AuditPayload
import dev.weg.alfa.modules.models.simpleModels.LendingStatus

data class LendingAudit(
    val lendingId: Int,
    val toolId: Int,
    val employeeId: Int,
    val lendingStatus: LendingStatus,
    val toolLent: Boolean
) : AuditPayload

fun Lending.toAuditPayload(): LendingAudit = LendingAudit(
    lendingId = this.id,
    toolId = this.tool.id,
    employeeId = this.employee.id,
    lendingStatus = this.status,
    toolLent = this.tool.isLoan
)
