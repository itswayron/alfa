package dev.weg.alfa.infra.audit.model

data class AuditDiff(
    val before: AuditPayload?,
    var after: AuditPayload?
)
