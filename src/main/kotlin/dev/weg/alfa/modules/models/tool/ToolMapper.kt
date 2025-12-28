package dev.weg.alfa.modules.models.tool

import dev.weg.alfa.infra.persistence.specification.ToolSpecificationBuilder
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import org.springframework.data.jpa.domain.Specification

fun ToolRequest.toEntity(
    subgroup: Subgroup,
): Tool = Tool(
    id = 0,
    name = this.name,
    description = this.description,
    maximumUsages = this.maximumUsages,
    actualUsages = 0,
    isLoan = false,
    subgroup = subgroup,
)

fun Tool.toResponse(): ToolResponse =
    ToolResponse(
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
    subgroup: Subgroup,
): Tool =
    Tool(
        id = this.id,
        subgroup = subgroup,
        name = patch.name ?: this.name,
        description = patch.description ?: this.description,
        maximumUsages = patch.maximumUsages ?: this.maximumUsages,
        actualUsages = this.actualUsages,
        isLoan = patch.isLoan ?: this.isLoan,
    )

fun ToolFilter.toSpecification(): Specification<Tool> =
    ToolSpecificationBuilder()
        .whereText(this.text)
        .whereSubgroup(this.subgroupId)
        .whereIsLoan(this.isLoan)
        .build()
