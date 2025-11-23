package dev.weg.alfa.modules.models.movement

import java.time.LocalDateTime

data class MovementRequest(
    val quantity: Double,
    val price: Double?,
    val observation: String?,
    val stockId: Int,
    val movementBatchId: Int?,
    val date: LocalDateTime = LocalDateTime.now(),
    val typeId: Int,
    val employeeId: Int,
    val statusId: Int,
    val sectorId: Int,
)

data class MovementPatch(
    val quantity: Double? = null,
    val price: Double? = null,
    val observation: String? = null,
    val movementBatchId: Int? = null,
    val statusId: Int? = null,
)

data class MovementResponse(
    val id: Int,
    val quantity: Double,
    val price: Double?,
    val date: LocalDateTime,
    val observation: String?,
    val stockId: Int,
    val stockItemName: String?,
    val movementBatchId: Int? = null,
    val movementBatchCode: String? = null,
    val type: String,
    val employee: String,
    val status: String,
    val sector: String,
)
