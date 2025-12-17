package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.movement.Movement
import dev.weg.alfa.modules.models.stock.Stock
import jakarta.persistence.Id
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MovementRepository : JpaRepository<Movement, Int>, JpaSpecificationExecutor<Movement> {

    // TODO: Integration Test : findLastMovementsAffectingAveragePrice should return only movements for the given stock
    // TODO: Integration Test : findLastMovementsAffectingAveragePrice should filter out movements where affectsAveragePrice = false
    // TODO: Integration Test : findLastMovementsAffectingAveragePrice should filter out movements with status != 'CONCLUIDO'
    // TODO: Integration Test : findLastMovementsAffectingAveragePrice should return results ordered by date descending
    // TODO: Integration Test : findLastMovementsAffectingAveragePrice should respect pagination and return limited number of records
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

    fun countByMovementBatchId(batchId: Int): Long

    fun findAllByStockId(stockId: Int): List<Movement>
}
