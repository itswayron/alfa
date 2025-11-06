package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.models.tool.Tool
import java.time.LocalDateTime

data class Lending(
    val id: Int = 0,
    val tool: Tool,
    val status: LendingStatus,
    val departureAt: LocalDateTime = LocalDateTime.now(),
    val estimatedReturnAt: LocalDateTime,
    val returnedAt: LocalDateTime? = null,
    val employee: Employee,
    val observation: String? = null,
)
