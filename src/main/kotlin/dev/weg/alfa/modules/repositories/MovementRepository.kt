package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.movement.Movement
import dev.weg.alfa.modules.models.movement.MovementType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MovementRepository : JpaRepository<Movement, Int>, JpaSpecificationExecutor<Movement> {

    @Query(
        """
       SELECT m FROM Movement m
       WHERE m.stock.id = :stockId AND
       m.type IN :types AND
       m.status = 'COMPLETED'
       ORDER BY m.date DESC
       """
    )
    fun findLastMovementsFromStockType(
        @Param("stockId") stockId: Int,
        @Param("types") types: Set<MovementType>
    ): List<Movement>

    fun findAllByMovementBatchId(batchId: Int, pageable: Pageable?): List<Movement>

    fun findAllByMovementBatchId(batchId: Int): List<Movement>

    fun countByMovementBatchId(batchId: Int): Long

    fun findAllByStockId(stockId: Int): List<Movement>

    fun findAllByStockIdOrderByDateAsc(id: Int): List<Movement>
}
