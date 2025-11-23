package dev.weg.alfa.infra.persistence.specification

import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.models.tool.Tool
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class ToolSpecificationBuilder {
    private val predicates = mutableListOf<(root: Root<Tool>, cb: CriteriaBuilder) -> Predicate>()

    fun whereText(text: String?): ToolSpecificationBuilder {
        if(!text.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${text}%"
                val descPredicate = cb.like(root.get("description"), pattern)
                val namePredicate = cb.like(root.get("name"), pattern)
                cb.or(descPredicate, namePredicate)
            }
        }
        return this
    }

    fun whereSubgroup(subgroupId: Int?): ToolSpecificationBuilder {
        if (subgroupId != null) {
            predicates += { root, cb ->
                cb.equal(root.get<Subgroup>("subgroup").get<Int>("id"), subgroupId)
            }
        }
        return this
    }

    fun whereIsLoan(isLoan: Boolean?): ToolSpecificationBuilder {
        if (isLoan != null) {
            predicates += { root, cb ->
                cb.equal(root.get<Boolean>("isLoan"), isLoan)
            }
        }
        return this
    }

    fun build(): Specification<Tool> {
        return Specification { root, _, cb ->
            val finalPredicates = predicates.map { it(root, cb) }.toTypedArray()
            cb.and(*finalPredicates)
        }
    }
}
