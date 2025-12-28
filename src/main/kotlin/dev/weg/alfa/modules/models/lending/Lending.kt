package dev.weg.alfa.modules.models.lending

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.models.tool.Tool
import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status_enum")
    @JdbcType(PostgreSQLEnumJdbcType::class)
    var status: LendingStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: Employee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    val tool: Tool,
) {
    fun returnWith(dto: ReturnLending, status: LendingStatus): Lending {
        if (dto.timeOfReturn.isBefore(departureTime)) {
            throw IllegalStateException("The time of return is before the departure time")
            // TODO: Create custom exception for this case
        }

        this.timeOfReturn = dto.timeOfReturn
        this.observation = dto.observation ?: this.observation
        this.status = status
        return this
    }
}
