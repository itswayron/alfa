package dev.weg.alfa.modules.models.position

data class PositionRequest(
    val floor: String,
    val side: String,
    val column: String,
    val box: String,
)

data class PositionPatch(
    val floor: String? = null,
    val side: String? = null,
    val column: String? = null,
    val box: String? = null,
)
