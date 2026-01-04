package dev.weg.alfa.modules.services

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.aspects.AuditContext
import dev.weg.alfa.infra.audit.model.ToolAction
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.tool.*
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class ToolService(
    private val toolRepository: ToolRepository,
    private val subgroupRepository: SubgroupRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("hasAuthority('MANAGE_ITEM')")
    @Auditable(action = ToolAction.CREATED)
    fun createTool(request: ToolRequest): ToolResponse {
        logger.info("Creating Tool with name: ${request.name}")
        val subgroup = subgroupRepository.findByIdOrThrow(request.subgroupID)
        val tool = toolRepository.save(request.toEntity(subgroup))
        val response = tool.toResponse()
        logger.info("Tool '${tool.name}' created with ID='${tool.id}'")
        AuditContext.created(tool.toAuditPayload())
        return response
    }

    @PreAuthorize("hasAuthority('VIEW_ITEM')")
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

    @PreAuthorize("hasAuthority('MANAGE_ITEM')")
    @Auditable(action = ToolAction.UPDATED)
    fun updateTool(command: Pair<Int, ToolPatch>): ToolResponse {
        val (id, request) = command
        logger.info("Updating tool with $id with name: ${request.name}.")
        val oldTool = toolRepository.findByIdOrThrow(id)

        val newSubgroup = if (request.subgroupID != null) {
            subgroupRepository.findByIdOrThrow(request.subgroupID)
        } else {
            oldTool.subgroup
        }

        val updatedTool = oldTool.applyPatch(
            patch = request,
            subgroup = newSubgroup
        )
        val newTool = toolRepository.save(updatedTool)
        logger.info("Successfully updated tool. Name='${newTool.name}' ID='${newTool.id}'")
        AuditContext.updated(oldTool.toAuditPayload(), newTool.toAuditPayload())
        return newTool.toResponse()
    }

    @PreAuthorize("hasAuthority('MANAGE_ITEM')")
    @Auditable(action = ToolAction.DELETED)
    fun deleteToolById(id: Int) {
        logger.info("Deleting Tool with id: $id.")
        val delete = toolRepository.findByIdOrThrow(id)
        toolRepository.delete(delete)
        logger.info("Tool with ID $id deleted with successfully.")
        AuditContext.deleted(delete.toAuditPayload())
    }
}
