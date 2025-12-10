package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.tool.*
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

// TODO: Unit Test : Should create tool when subgroup exists
// TODO: Unit Test : Should throw when creating tool with non-existent subgroup

// TODO: Unit Test : Should return filtered tools using specification
// TODO: Unit Test : Should correctly map paginated tools to DTO

// TODO: Unit Test : Should update tool when it exists
// TODO: Unit Test : Should update tool with new subgroup when subgroupID changes
// TODO: Unit Test : Should throw when updating non-existent tool
// TODO: Unit Test : Should throw when updating tool with non-existent subgroup

// TODO: Unit Test : Should delete tool when it exists
// TODO: Unit Test : Should throw when deleting non-existent tool
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

    fun getFilteredTools(
        filter: ToolFilter,
        pageable: Pageable,
    ): PageDTO<ToolResponse> {
        logger.info("Fetching tools with filters='{}'", filter)
        val spec = filter.toSpecification()
        val page = toolRepository.findAll(spec, pageable)
        logger.info("Retrieved {} tools matching the criteria.", page.content.size)
        val response = page.map { it.toResponse() }.toDTO()
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
