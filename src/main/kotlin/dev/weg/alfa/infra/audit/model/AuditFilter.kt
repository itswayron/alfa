package dev.weg.alfa.infra.audit.model

import dev.weg.alfa.infra.persistence.specification.AuditSpecificationBuilder
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

data class AuditFilter(
    val actor: String? = null,
    val action: String? = null,
    val beforeContains: String? = null,
    val afterContains: String? = null,
    val timestampFrom: Instant? = null,
    val timestampTo: Instant? = null,
)

fun AuditFilter.toSpecification(): Specification<Audit> =
    AuditSpecificationBuilder()
        .whereActor(actor)
        .whereAction(action)
        .whereBeforeContains(beforeContains)
        .whereAfterContains(afterContains)
        .whereTimestampFrom(timestampFrom)
        .whereTimestampTo(timestampTo)
        .build()
