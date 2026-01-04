package dev.weg.alfa.modules.models.businessPartner

import dev.weg.alfa.infra.audit.model.AuditPayload

data class BusinessPartnerAudit(
    val id: Int,
    val name: String,
    val cnpj: String,
    val relation: String,
) : AuditPayload

fun BusinessPartner.toAuditPayload() = BusinessPartnerAudit(
    id = this.id,
    name = this.name,
    cnpj = this.cnpj,
    relation = this.relation,
)
