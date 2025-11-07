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
}
