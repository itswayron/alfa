package dev.weg.alfa.infra.persistence.specification

import dev.weg.alfa.infra.audit.model.Audit
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

class AuditSpecificationBuilder {

    private val predicates =
        mutableListOf<(root: Root<Audit>, cb: CriteriaBuilder) -> Predicate>()

    fun whereActor(actor: String?): AuditSpecificationBuilder {
        if (!actor.isNullOrBlank()) {
            predicates += { root, cb ->
                cb.equal(root.get<String>("actor"), actor)
            }
        }
        return this
    }

    fun whereAction(action: String?): AuditSpecificationBuilder {
        if (!action.isNullOrBlank()) {
            predicates += { root, cb ->
                cb.equal(root.get<String>("action"), action)
            }
        }
        return this
    }

    fun whereBeforeContains(text: String?): AuditSpecificationBuilder {
        if (!text.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${text.lowercase()}%"
                cb.like(cb.lower(root.get("before")), pattern)
            }
        }
        return this
    }

    fun whereAfterContains(text: String?): AuditSpecificationBuilder {
        if (!text.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${text.lowercase()}%"
                cb.like(cb.lower(root.get("after")), pattern)
            }
        }
        return this
    }

    fun whereTimestampFrom(start: Instant?): AuditSpecificationBuilder {
        if (start != null) {
            predicates += { root, cb ->
                cb.greaterThanOrEqualTo(root.get("timestamp"), start)
            }
        }
        return this
    }

    fun whereTimestampTo(end: Instant?): AuditSpecificationBuilder {
        if (end != null) {
            predicates += { root, cb ->
                cb.lessThanOrEqualTo(root.get("timestamp"), end)
            }
        }
        return this
    }

    fun build(): Specification<Audit> {
        return Specification { root, _, cb ->
            cb.and(*predicates.map { it(root, cb) }.toTypedArray())
        }
    }
}
