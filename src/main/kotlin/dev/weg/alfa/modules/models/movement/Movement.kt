package dev.weg.alfa.modules.models.movement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.movementBatch.MovementBatch
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.models.stock.Stock
import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
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
    val observation: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    val stock: Stock,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", nullable = true)
    val movementBatch: MovementBatch?,

    @Enumerated(EnumType.STRING)
    @Column(name = "type_id", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val type: MovementType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: Employee,

    @Enumerated(EnumType.STRING)
    @Column(name = "status_enum", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val status: MovementStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    val sector: Sector,
) {
    fun getSignedAmount(): Double {
        return type.quantitySign * quantity
    }

    fun getSignedPrice(): Double {
        return quantity * type.quantitySign * (price ?: 0.0)
    }
}
