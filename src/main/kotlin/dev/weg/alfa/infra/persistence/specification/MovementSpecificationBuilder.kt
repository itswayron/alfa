package dev.weg.alfa.infra.persistence.specification

import dev.weg.alfa.modules.models.movement.Movement
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

class MovementSpecificationBuilder {

    private val predicates = mutableListOf<(root: Root<Movement>, cb: CriteriaBuilder) -> Predicate>()

    fun whereStockId(stockId: Int?): MovementSpecificationBuilder {
        if (stockId != null) {
            predicates += { root, cb ->
                cb.equal(root.join<Movement, Any>("stock").get<Int>("id"), stockId)
            }
        }
        return this
    }

    fun whereBatchId(batchId: Int?): MovementSpecificationBuilder {
        if (batchId != null) {
            predicates += { root, cb ->
                cb.equal(root.join<Movement, Any>("movementBatch").get<Int>("id"), batchId)
            }
        }
        return this
    }

    fun whereType(typeId: Int?): MovementSpecificationBuilder {
        if (typeId != null) {
            predicates += { root, cb ->
                cb.equal(root.join<Movement, Any>("type").get<Int>("id"), typeId)
            }
        }
        return this
    }

    fun whereStatus(statusId: Int?): MovementSpecificationBuilder {
        if (statusId != null) {
            predicates += { root, cb ->
                cb.equal(root.join<Movement, Any>("status").get<Int>("id"), statusId)
            }
        }
        return this
    }

    fun whereSector(sectorId: Int?): MovementSpecificationBuilder {
        if (sectorId != null) {
            predicates += { root, cb ->
                cb.equal(root.join<Movement, Any>("sector").get<Int>("id"), sectorId)
            }
        }
        return this
    }

    fun whereEmployee(employeeId: Int?): MovementSpecificationBuilder {
        if (employeeId != null) {
            predicates += { root, cb ->
                cb.equal(root.join<Movement, Any>("employee").get<Int>("id"), employeeId)
            }
        }
        return this
    }

    fun whereObservation(text: String?): MovementSpecificationBuilder {
        if (!text.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${text.lowercase()}%"
                cb.like(cb.lower(root.get("observation")), pattern)
            }
        }
        return this
    }

    fun whereDateFrom(start: LocalDateTime?): MovementSpecificationBuilder {
        if (start != null) {
            predicates += { root, cb ->
                cb.greaterThanOrEqualTo(root.get("date"), start)
            }
        }
        return this
    }

    fun whereDateTo(end: LocalDateTime?): MovementSpecificationBuilder {
        if (end != null) {
            predicates += { root, cb ->
                cb.lessThanOrEqualTo(root.get("date"), end)
            }
        }
        return this
    }

    fun build(): Specification<Movement> {
        return Specification { root, _, cb ->
            cb.and(*predicates.map { it(root, cb) }.toTypedArray())
        }
    }
}
