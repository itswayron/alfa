package dev.weg.alfa.modules.models.tool

import dev.weg.alfa.infra.audit.model.AuditPayload

data class ToolAudit(
    val id: Int,
    val name: String,
    val description: String,
    val maximumUsages: Int,
    val actualUsages: Int,
    val subgroupId: Int,
    val isLoan: Boolean,
) : AuditPayload

fun Tool.toAuditPayload(): ToolAudit = ToolAudit(
    id = this.id,
    name = this.name,
    description = this.description,
    maximumUsages = this.maximumUsages,
    actualUsages = this.actualUsages,
    subgroupId = this.subgroup.id,
    isLoan = this.isLoan
)
