package dev.weg.alfa.modules.models.mappers

import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.position.PositionPatch
import dev.weg.alfa.modules.models.position.PositionRequest

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
