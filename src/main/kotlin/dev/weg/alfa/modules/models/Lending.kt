package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.employee.Employee
import java.time.LocalDateTime

data class Lending(
    val id: Int,
    val tool: Tool,
    val status: String,
    val departureTime: LocalDateTime = LocalDateTime.now(),
    val estimatedReturn: LocalDateTime,
    val timeOfReturn: LocalDateTime,
    val employee: Employee,
    val observation: String,
)
