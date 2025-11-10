package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.stock.Stock
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, Int> {

    @Query(
        """
        SELECT s FROM Stock s WHERE
         (:text IS NULL OR LOWER(CAST(s.item.code AS string)) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')))
        AND (:groupId IS NULL OR s.item.group.id = :groupId)
        AND (:subgroupId IS NULL OR s.item.subgroup.id = :subgroupId)
        AND (:supplierId IS NULL OR s.item.mainSupplier.id = :supplierId)
        AND (:isActive IS NULL OR s.item.isActive = :isActive)
        """
    )
    fun findFiltered(
        @Param("text") text: String?,
        @Param("groupId") groupId: Int?,
        @Param("subgroupId") subgroupId: Int?,
        @Param("supplierId") supplierId: Int?,
        @Param("isActive") isActive: Boolean? = true,
        pageable: Pageable,
    ): List<Stock>
}
