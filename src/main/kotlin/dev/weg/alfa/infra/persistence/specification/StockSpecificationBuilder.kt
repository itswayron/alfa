package dev.weg.alfa.infra.persistence.specification

import dev.weg.alfa.modules.models.stock.Stock
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class StockSpecificationBuilder {
    private val predicates = mutableListOf<(root: Root<Stock>, cb: CriteriaBuilder) -> Predicate>()

    fun whereText(text: String?): StockSpecificationBuilder {
        if (!text.isNullOrBlank()) {
            predicates += { root, cb ->
                val pattern = "%${text.lowercase()}%"
                val item = root.join<Stock, Any>("item")

                val codePredicate = cb.like(cb.lower(item.get("code")), pattern)
                val descriptionPredicate = cb.like(cb.lower(item.get("description")), pattern)
                val dimensionsPredicate = cb.like(cb.lower(item.get("dimensions")), pattern)
                val materialPredicate = cb.like(cb.lower(item.get("material")), pattern)

                cb.or(codePredicate,descriptionPredicate, dimensionsPredicate, materialPredicate)
            }
        }
        return this
    }

    fun whereGroup(groupId: Int?): StockSpecificationBuilder {
        if (groupId != null) {
            predicates += { root, cb ->
                val item = root.join<Stock, Any>("item")
                cb.equal(item.join<Any, Any>("group").get<Int>("id"), groupId)
            }
        }
        return this
    }

    fun whereSubgroup(subgroupId: Int?): StockSpecificationBuilder {
        if (subgroupId != null) {
            predicates += { root, cb ->
                val item = root.join<Stock, Any>("item")
                cb.equal(item.join<Any, Any>("subgroup").get<Int>("id"), subgroupId)
            }
        }
        return this
    }

    fun whereSupplier(supplierId: Int?): StockSpecificationBuilder {
        if (supplierId != null) {
            predicates += { root, cb ->
                val item = root.join<Stock, Any>("item")
                cb.equal(item.join<Any, Any>("mainSupplier").get<Int>("id"), supplierId)
            }
        }
        return this
    }

    fun whereActive(isActive: Boolean?): StockSpecificationBuilder {
        if (isActive != null) {
            predicates += { root, cb ->
                val item = root.join<Stock, Any>("item")
                cb.equal(item.get<Boolean>("isActive"), isActive)
            }
        }
        return this
    }

    fun build(): Specification<Stock> {
        return Specification { root, query, cb ->
            val finalPredicates = predicates.map { it(root, cb) }.toTypedArray()
            cb.and(*finalPredicates)
        }
    }
}
