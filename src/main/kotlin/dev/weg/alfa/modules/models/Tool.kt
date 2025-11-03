package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.simpleModels.Subgroup

data class Tool(
    val id: Int,
    val description: String,
    val maximumUsages: Int,
    val actualUsages: Int,
    val subgroup: Subgroup,
    val isLoan: Boolean,
)
