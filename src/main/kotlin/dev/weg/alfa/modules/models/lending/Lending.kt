package dev.weg.alfa.modules.models.lending

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.models.tool.Tool
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
@Table(name = "lending")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Lending(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val departureTime: LocalDateTime = LocalDateTime.now(),
    val estimatedReturn: LocalDateTime,
    var timeOfReturn: LocalDateTime? = null,
    var observation: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    var status: LendingStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: Employee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    val tool: Tool,
)
