package dev.weg.alfa.modules.models.mappers

import dev.weg.alfa.modules.models.simpleModels.Subgroup
import dev.weg.alfa.modules.models.tool.Tool
import dev.weg.alfa.modules.models.tool.ToolPatch
import dev.weg.alfa.modules.models.tool.ToolResponse

fun ToolResponse.toEntity(
    subgroup: Subgroup,
): Tool = Tool(
    id = 0,
    name = this.name,
    description = this.description,
    maximumUsages = this.maximumUsages,
    actualUsages = this.actualUsages,
    isLoan = this.isLoan,
    subgroup = subgroup,
)

fun Tool.toResponse(): ToolResponse = ToolResponse(
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
    patch: ToolPatch,
    subgroup: Subgroup?
): Tool =
    Tool(
        id = this.id,
        name = patch.name ?: this.name,
        description = patch.description ?: this.description,
        maximumUsages = patch.maximumUsages ?: this.maximumUsages,
        actualUsages = this.actualUsages,
        subgroup = subgroup ?: this.subgroup,
        isLoan = patch.isLoan ?: this.isLoan,
    )
