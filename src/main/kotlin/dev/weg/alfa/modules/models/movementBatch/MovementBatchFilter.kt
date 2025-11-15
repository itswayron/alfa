package dev.weg.alfa.modules.models.movementBatch

import java.time.LocalDateTime

data class MovementBatchFilter(
    val code: String?,
    val document: String?,
    val partnerId: Int?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
)
