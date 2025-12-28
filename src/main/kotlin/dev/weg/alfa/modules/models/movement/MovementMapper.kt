package dev.weg.alfa.modules.models.movement

import dev.weg.alfa.infra.persistence.specification.MovementSpecificationBuilder
import dev.weg.alfa.modules.models.employee.Employee
import dev.weg.alfa.modules.models.movementBatch.MovementBatch
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.models.stock.Stock
import org.springframework.data.jpa.domain.Specification

fun MovementRequest.toEntity(
    stock: Stock,
    movementBatch: MovementBatch?,
    employee: Employee,
    sector: Sector
): Movement = Movement(
    id = 0,
    quantity = quantity,
    price = price,
    date = date,
    observation = observation,
    type = type,
    status = status,
    stock = stock,
    movementBatch = movementBatch,
    employee = employee,
    sector = sector
)

fun Movement.toResponse(): MovementResponse =
    MovementResponse(
        id = id,
        quantity = quantity,
        price = price,
        date = date,
        observation = observation,
        stockId = stock.id,
        stockItemName = stock.item.description,
        movementBatchId = movementBatch?.id,
        movementBatchCode = movementBatch?.code,
        type = type.name,
        employee = employee.name,
        status = status.name,
        sector = sector.name
    )

fun Movement.applyPatch(
    patch: MovementPatch,
    movementBatch: MovementBatch? = this.movementBatch,
): Movement =
    this.copy(
        quantity = patch.quantity ?: this.quantity,
        price = patch.price ?: this.price,
        observation = patch.observation ?: this.observation,
        movementBatch = patch.movementBatchId?.let { movementBatch } ?: this.movementBatch,
        status = patch.status ?: this.status
    )

fun MovementFilter.toSpecification(): Specification<Movement> =
    MovementSpecificationBuilder()
        .whereStockId(stockId)
        .whereBatchId(batchId)
        .whereType(type)
        .whereStatus(status)
        .whereSector(sectorId)
        .whereEmployee(employeeId)
        .whereObservation(observation)
        .whereDateFrom(dateFrom)
        .whereDateTo(dateTo)
        .build()
