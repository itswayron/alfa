package dev.weg.alfa.modules.models.stock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.item.Item
import dev.weg.alfa.modules.models.movement.Movement
import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.simpleModels.Sector
import jakarta.persistence.*

@Entity
@Table(name = "stock")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Stock(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @ManyToOne
    @JoinColumn(name = "item_id")
    val item: Item,

    val currentAmount: Double,
    val minimumAmount: Double?,
    val maximumAmount: Double?,
    val averagePrice: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    val sector: Sector,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    val position: Position,
) {
    fun applyMovement(movement: Movement, lastMovements: List<Movement>): Stock {
        val newAmount = currentAmount + movement.getSignedAmount()
        val newAveragePrice = recalculateAveragePrice(lastMovements + movement)
        return copy(
            currentAmount = newAmount,
            averagePrice = newAveragePrice
        )
    }

    private fun recalculateAveragePrice(movements: List<Movement>): Double {
        val affectingMovements =
            movements.filter {
                it.type.affectsAveragePrice && it.price != null
            }.takeLast(10)

        if (affectingMovements.isEmpty()) {
            return averagePrice
        }

        val totalAmount = affectingMovements.sumOf { it.getSignedAmount() }
        val totalValue = affectingMovements.sumOf { it.getSignedPrice() }

        val averagePrice = if (totalAmount != 0.0) {
            totalValue / totalAmount
        } else {
            this.averagePrice
        }

        return averagePrice
    }
}
