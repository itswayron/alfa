package dev.weg.alfa.modules.models.position

data class PositionCreationRequest(
    val floor: String,
    val side: String,
    val column: String,
    val box: String
) {
    fun toEntity() = Position(
        floor = this.floor,
        side = this.side,
        column = this.column,
        box = this.box
    )
}
