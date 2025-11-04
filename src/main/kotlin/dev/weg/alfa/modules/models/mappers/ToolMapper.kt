package dev.weg.alfa.modules.models.mappers

import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.models.tool.*

fun ResponseTool.toEntity(
    subgroup: Subgroup,
): Tool = Tool(
    id = 0,
    name = this.name,
    description = this.description,
    maximumUsages = this.maximumUsages,
    actualUsages = this.actualUsages,
    subgroup = subgroup,
    isLoan = this.isLoan,
)

fun Tool.toResponse(): ResponseTool = ResponseTool(
    id = this.id,
    name = this.name,
    description = this.description,
    maximumUsages = this.maximumUsages,
    actualUsages = this.actualUsages,
    subgroupID = this.subgroup.id,
    subgroupName = this.subgroup.name,
    isLoan = this.isLoan,
)

fun Tool.applyPatch(
    patch: UpdateTool,
    subgroup: Subgroup?
): Tool =
    Tool(
        id = this.id,
        name = patch.name ?:this.name,
        description = patch.description ?: this.description,
        maximumUsages = patch.maximumUsages ?: this.maximumUsages,
        actualUsages = patch.actualUsages ?: this.actualUsages,
        subgroup = subgroup ?: this.subgroup,
        isLoan = patch.isLoan ?: this.isLoan,
    )