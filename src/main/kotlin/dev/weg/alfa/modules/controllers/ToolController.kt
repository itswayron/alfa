package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.tool.ToolPatch
import dev.weg.alfa.modules.models.tool.ToolRequest
import dev.weg.alfa.modules.models.tool.ToolResponse
import dev.weg.alfa.modules.services.ToolService
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
    fun getAllTool(): ResponseEntity<List<ToolResponse>> {
        val response = service.getAllTool()
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
