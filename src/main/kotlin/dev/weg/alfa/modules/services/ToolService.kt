package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.tool.*
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.findByIdOrThrow
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ToolService(
    private val toolRepository: ToolRepository,
    private val subgroupRepository: SubgroupRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createTool(request: ToolRequest): ToolResponse {
        logger.info("Creating Tool with name: ${request.name}")
        val subgroup = subgroupRepository.findByIdOrThrow(request.subgroupID)
        val tool = toolRepository.save(request.toEntity(subgroup))
        val response = tool.toResponse()
        return response
    }

    fun getAllTool(): List<ToolResponse> {
        logger.info("Retrieving all tools from the database.")
        val tools = toolRepository.findAll()
        logger.info("Found ${tools.size} tools on the database.")
        val response = tools.map { it.toResponse() }
        return response
    }

    fun updateTool(command: Pair<Int, ToolPatch>): ToolResponse {
        val (id, request) = command
        logger.info("Updating tool with $id with name: ${request.name}.")
        val oldTool = toolRepository.findByIdOrThrow(id)
        val newSubgroupId = request.subgroupID
        val newSubgroup = if (newSubgroupId != null) {
            subgroupRepository.findByIdOrThrow(newSubgroupId)
        } else {
            null
        }
        val updatedTool = oldTool.applyPatch(
            patch = request,
            subgroup = newSubgroup
        )
        val newTool = toolRepository.save(updatedTool)
        return newTool.toResponse()
    }

    fun deleteToolById(id: Int) {
        logger.info("Deleting Tool with id: $id.")
        val delete = toolRepository.findByIdOrThrow(id)
        toolRepository.delete(delete)
        logger.info("Tool with ID $id deleted with successfully.")
    }
}
