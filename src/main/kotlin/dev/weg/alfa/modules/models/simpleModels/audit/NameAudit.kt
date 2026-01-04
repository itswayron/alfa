package dev.weg.alfa.modules.models.simpleModels.audit

import dev.weg.alfa.infra.audit.model.AuditPayload
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.models.simpleModels.Subgroup

data class NameAudit(
    val id: Int,
    val name: String,
) : AuditPayload

fun Group.toAuditPayload(): NameAudit = NameAudit(
    id = this.id,
    name = this.name,
)

fun MeasurementUnity.toAuditPayload(): NameAudit = NameAudit(
    id = this.id,
    name = this.name,
)

fun Subgroup.toAuditPayload(): NameAudit = NameAudit(
    id = this.id,
    name = this.name,
)

fun Sector.toAuditPayload(): NameAudit = NameAudit(
    id = this.id,
    name = this.name,
)
