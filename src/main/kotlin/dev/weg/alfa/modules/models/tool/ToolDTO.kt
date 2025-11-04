package dev.weg.alfa.modules.models.tool

import jakarta.persistence.Id

data class RequestTool(
    val name: String,
    val description: String,
    val maximumUsages: Int,
    val actualUsages: Int,
    val subgroupID: Int,
    val isLoan: Boolean,
)
data class ResponseTool(
    val id: Int,
    val name: String,
    val description: String,
    val maximumUsages: Int,
    val actualUsages: Int,
    val subgroupID: Int,
    val subgroupName: String,
    val isLoan: Boolean,
)

data class UpdateTool(
    val name: String?,
    val description: String?,
    val maximumUsages: Int?,
    val actualUsages: Int?,
    val subgroupID: Int?,
    val isLoan: Boolean?,
)

