package dev.weg.alfa.modules.models.lending

import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.models.tool.Tool
import dev.weg.alfa.modules.models.tool.toResponse
import java.time.LocalDateTime

fun LendingRequest.toEntity(
    status: LendingStatus,
    employee: Employee,
    tool: Tool
): Lending =
    Lending(
        id = 0,
        status = status,
        departureTime = this.departureTime ?: LocalDateTime.now(),
        timeOfReturn = null,
        employee = employee,
        tool = tool,
        observation = this.observation,
        estimatedReturn = this.estimatedReturn,
    )

fun Lending.toResponse(): LendingResponse =
    LendingResponse(
        id = this.id,
        departureTime = this.departureTime,
        status = this.status,
        estimatedReturn = this.estimatedReturn,
        timeOfReturn = this.timeOfReturn,
        observation = this.observation,
        employee = this.employee,
        tool = this.tool.toResponse()
    )

fun Lending.applyPatch(patch: LendingPatch, newStatus: LendingStatus? = null): Lending =
    Lending(
        id = this.id,
        status = newStatus ?: this.status,
        estimatedReturn = patch.estimatedReturn ?: this.estimatedReturn,
        observation = patch.observation ?: this.observation,
        timeOfReturn = patch.timeOfReturn ?: this.timeOfReturn,
        departureTime = this.departureTime,
        employee = this.employee,
        tool = this.tool
    )

fun Lending.returnWith(dto: ReturnLending, status: LendingStatus): Lending {
    this.timeOfReturn = dto.timeOfReturn
    this.observation = dto.observation ?: this.observation
    this.status = status
    return this
}
