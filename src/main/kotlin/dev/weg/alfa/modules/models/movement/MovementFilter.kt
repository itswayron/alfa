package dev.weg.alfa.modules.models.movement

import java.time.LocalDateTime

data class MovementFilter(
    val stockId: Int? = null,
    val batchId: Int? = null,
    val typeId: Int? = null,
    val statusId: Int? = null,
    val sectorId: Int? = null,
    val employeeId: Int? = null,
    val observation: String? = null,
    val dateFrom: LocalDateTime? = null,
    val dateTo: LocalDateTime? = null,
)
