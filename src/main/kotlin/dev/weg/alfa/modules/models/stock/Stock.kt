package dev.weg.alfa.modules.models.stock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.Position
import dev.weg.alfa.modules.models.item.Item
import dev.weg.alfa.modules.models.simpleModels.Sector
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

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
)
