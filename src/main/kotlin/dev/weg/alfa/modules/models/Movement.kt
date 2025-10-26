package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.MovementStatus.MovementStatus
import dev.weg.alfa.modules.models.simpleModels.MovementType
import dev.weg.alfa.modules.models.simpleModels.Sector
import java.time.LocalDateTime

data class Movement(
    val id: Int,
    val stock: Stock,
    val productionOrder: ProductionOrder?,
    val quantity: Double,
    val price: Double?,
    val type: MovementType,
    val date: LocalDateTime = LocalDateTime.now(),
    val employee: Employee,
    val observation: String,
    val status: MovementStatus,
    val sector: Sector,
)
