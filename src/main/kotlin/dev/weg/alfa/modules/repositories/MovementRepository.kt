package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.movement.Movement
import dev.weg.alfa.modules.models.stock.Stock
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MovementRepository : JpaRepository<Movement, Int> {
    @Query(
        """
        SELECT m FROM Movement m 
        WHERE m.stock = :stock AND
        m.type.affectsAveragePrice = true
        AND m.status.name = 'CONCLUIDO'
        ORDER BY m.date DESC
        """
    )
    fun findLastMovementsAffectingAveragePrice(
        @Param("stock") stock: Stock,
        pageable: Pageable
    ): List<Movement>

    fun findAllByMovementBatchId(batchId: Int, pageable: Pageable?): List<Movement>

    fun findAllByMovementBatchId(batchId: Int): List<Movement>

    @Query(
        """SELECT m FROM Movement m
           WHERE (:stockId IS NULL OR m.stock.id = :stockId)
           AND ( :text IS NULL OR 
                 LOWER(CAST(CAST(m.stock.item.code AS string) AS string)) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')) OR
                 LOWER(CAST(CAST(m.stock.item.description AS string) AS string)) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%'))
               )
           AND (:typeId IS NULL OR m.type.id = :typeId)
           AND (:statusId IS NULL OR m.status.id = :statusId)
    """
    )
    fun findFiltered(
        @Param("stockId") stockId: Int?,
        @Param("text") text: String?,
        @Param("typeId") typeId: Int?,
        @Param("statusId") statusId: Int?,
        pageable: Pageable
    ): List<Movement>
}
