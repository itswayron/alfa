package dev.weg.alfa.modules.models.position

fun PositionRequest.toEntity(): Position =
    Position(
        floor = this.floor,
        side = this.side,
        column = this.column,
        box = this.box
    )

fun Position.applyPatch(patch: PositionPatch): Position =
    Position(
        id = this.id,
        floor = patch.floor ?: this.floor,
        side = patch.side ?: this.side,
        box = patch.box ?: this.box,
        column = patch.column ?: this.column,
    )
