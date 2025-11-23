package dev.weg.alfa.infra.persistence.specification

import dev.weg.alfa.modules.models.movementBatch.MovementBatch
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

class MovementBatchSpecificationBuilder {
    private val predicates = mutableListOf<(root: Root<MovementBatch>, cb: CriteriaBuilder) -> Predicate>()

    fun whereCode(code: String?): MovementBatchSpecificationBuilder {
        if (!code.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${code.lowercase()}%"
                cb.like(cb.lower(root.get("code")), pattern)
            }
        }
        return this
    }

    fun whereDocument(document: String?): MovementBatchSpecificationBuilder {
        if (!document.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${document.lowercase()}%"
                cb.like(cb.lower(root.get("document")), pattern)
            }
        }
        return this
    }

    fun whereBusinessPartner(partnerId: Int?): MovementBatchSpecificationBuilder {
        if (partnerId != null) {
            predicates += { root, cb ->
                val join = root.join<MovementBatch, Any>("businessPartner")
                cb.equal(join.get<Int>("id"), partnerId)
            }
        }
        return this
    }

    fun whereDateFrom(start: LocalDateTime?): MovementBatchSpecificationBuilder {
        if (start != null) {
            predicates += { root, cb ->
                cb.greaterThanOrEqualTo(root.get("date"), start)
            }
        }
        return this
    }

    fun whereDateTo(end: LocalDateTime?): MovementBatchSpecificationBuilder {
        if (end != null) {
            predicates += { root, cb ->
                cb.lessThanOrEqualTo(root.get("date"), end)
            }
        }
        return this
    }

    fun build(): Specification<MovementBatch> {
        return Specification { root, _, cb ->
            val finalPredicates = predicates.map { it(root, cb) }.toTypedArray()
            cb.and(*finalPredicates)
        }
    }
}
