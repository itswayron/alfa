package dev.weg.alfa.modules.models.movement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.productionOrder.ProductionOrder
import dev.weg.alfa.modules.models.simpleModels.MovementStatus
import dev.weg.alfa.modules.models.simpleModels.MovementType
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.models.stock.Stock
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "movement")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Movement(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val quantity: Double,
    val price: Double?,
    val date: LocalDateTime = LocalDateTime.now(),
    val observation: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    val stock: Stock,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", nullable = true)
    val productionOrder: ProductionOrder?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    val type: MovementType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: Employee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    val status: MovementStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    val sector: Sector,
)