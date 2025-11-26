package dev.weg.alfa.modules.models.lending

import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.models.tool.ToolResponse
import java.time.LocalDateTime

data class LendingRequest(
    val estimatedReturn: LocalDateTime,
    val departureTime: LocalDateTime?,
    val employeeId: Int,
    val toolId: Int,
    val observation: String? = null
)

data class LendingResponse(
    val id: Int,
    val departureTime: LocalDateTime,
    val status: LendingStatus,
    val estimatedReturn: LocalDateTime,
    val timeOfReturn: LocalDateTime? = null,
    val observation: String? = null,
    val employee: Employee,
    val tool: ToolResponse,
)

data class LendingPatch(
    val estimatedReturn: LocalDateTime? = null,
    val observation: String? = null,
    val timeOfReturn: LocalDateTime? = null
)

data class ReturnLending(
    val timeOfReturn: LocalDateTime = LocalDateTime.now(),
    val observation: String? = null,
)
