package dev.weg.alfa.modules.services

import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.dtos.toDTO
import dev.weg.alfa.modules.models.tool.*
import dev.weg.alfa.modules.repositories.ToolRepository
import dev.weg.alfa.modules.repositories.simpleEntities.SubgroupRepository
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
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

    fun getAllTool(
        searchTerm: String?,
        subgroupId: Int?,
        isLoan: Boolean?,
        conservationState: String?,
        pageable: Pageable
    ): PageDTO<ToolResponse> {
        logger.info("Fetching tools with filters.")
        val filter = buildfilterSpecification(searchTerm, subgroupId, isLoan, conservationState)
        val page = toolRepository.findAll(filter, pageable)
        logger.info("Retrieved {} tools matching the criteria.", page.content.size)
        return page.map { it.toResponse() }.toDTO()
    }

    private fun buildfilterSpecification(
        searchTerm: String?,
        subgroupId: Int?,
        isLoan: Boolean?,
        conservationState: String?
    ): Specification<Tool>? {
        var filter: Specification<Tool>? = null

        fun Specification<Tool>?.andIf(
            condition: Boolean,
            filterBuilder: () -> Specification<Tool>
        ): Specification<Tool>? {
            return if (condition) this?.and(filterBuilder()) ?: filterBuilder() else this
        }

        filter = filter.andIf(!searchTerm.isNullOrBlank()) {
            Specification { root, query, critery ->
                val descPredicate = critery.like(root.get("description"), "%$searchTerm%")
                val namePredicate = critery.like(root.get("name"), "%$searchTerm%")
                critery.or(descPredicate, namePredicate)
            }
        }

        filter = filter.andIf(subgroupId != null) {
            Specification { root, query, critery -> critery.equal(root.get<Int>("subgroupId"), subgroupId) }
        }

        filter = filter.andIf(isLoan != null) {
            Specification { root, query, critery -> critery.equal(root.get<Boolean>("isLoan"), isLoan) }
        }

        filter = filter.andIf(!conservationState.isNullOrBlank()) {
            Specification { root, query, critery -> critery.equal(root.get<String>("conservationState"), conservationState) }
        }
        return filter
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
