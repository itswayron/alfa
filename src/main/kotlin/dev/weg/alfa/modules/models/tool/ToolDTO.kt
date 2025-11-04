package dev.weg.alfa.modules.models.tool

data class ToolRequest(
    val name: String,
    val description: String,
    val maximumUsages: Int,
    val actualUsages: Int,
    val subgroupID: Int,
    val isLoan: Boolean,
)
data class ToolResponse(
    val id: Int,
    val name: String,
    val description: String,
    val maximumUsages: Int,
    val actualUsages: Int,
    val subgroupID: Int,
    val subgroupName: String,
    val isLoan: Boolean,
)

data class ToolPatch(
    val name: String?=null,
    val description: String?=null,
    val maximumUsages: Int?=null,
    val actualUsages: Int?=null,
    val subgroupID: Int?=null,
    val isLoan: Boolean?=null,
)

