package dev.weg.alfa.modules.models.movementBatch

import dev.weg.alfa.modules.models.movement.MovementRequest
import dev.weg.alfa.modules.models.movement.MovementResponse
import java.time.LocalDateTime

data class MovementBatchPatch(
    val code: String? = null,
    val document: String? = null,
    val date: LocalDateTime? = null,
    val mainSupplierId: Int? = null,
    val observation: String? = null,
)

data class MovementBatchRequest(
    val code: String,
    val document: String?,
    val partnerId: Int? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val observation: String? = null,
    val movementsList: List<MovementRequest>,
)

data class MovementBatchResponseWithList(
    val id: Int,
    val code: String,
    val document: String?,
    val date: LocalDateTime,
    val businessPartner: String?,
    val observation: String? = null,
    val movementsSize: Long,
    val movementList: List<MovementResponse>,
)

data class MovementBatchResponse(
    val id: Int,
    val code: String,
    val document: String?,
    val date: LocalDateTime,
    val businessPartner: String?,
    val observation: String? = null,
    val movementsSize: Long,
)
