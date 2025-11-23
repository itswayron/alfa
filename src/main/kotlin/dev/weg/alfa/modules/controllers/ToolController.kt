package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.tool.ToolFilter
import dev.weg.alfa.modules.models.tool.ToolPatch
import dev.weg.alfa.modules.models.tool.ToolRequest
import dev.weg.alfa.modules.models.tool.ToolResponse
import dev.weg.alfa.modules.services.ToolService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.TOOL)
class ToolController(private val service: ToolService) {

    @PostMapping
    fun createTool(@RequestBody request: ToolRequest): ResponseEntity<ToolResponse> {
        val response = service.createTool(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllTools(
        @RequestParam(required = false) text: String?,
        @RequestParam(required = false) subgroupId: Int?,
        @RequestParam(required = false) isLoan: Boolean?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "description") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<ToolResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
        val filter = ToolFilter(text, subgroupId, isLoan)
        val response = service.getFilteredTools(filter, pageable)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updateTool(
        @PathVariable id: Int,
        @RequestBody request: ToolPatch
    ): ResponseEntity<ToolResponse> {
        val response = service.updateTool(command = Pair(id, request))
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteTool(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteToolById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
