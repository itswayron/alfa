package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.tool.RequestTool
import dev.weg.alfa.modules.models.tool.ResponseTool
import dev.weg.alfa.modules.models.tool.UpdateTool
import dev.weg.alfa.modules.services.ToolService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.TOOL)
class ToolController(private val service: ToolService) {

    @PostMapping
    fun createTool(@RequestBody request: RequestTool): ResponseEntity<ResponseTool> {
        val response = service.createTool(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllTool(): ResponseEntity<List<ResponseTool>> {
        val response = service.getAllTool()
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{id}")
    fun updateTool(
        @PathVariable id: Int,
        @RequestBody request: UpdateTool
    ): ResponseEntity<ResponseTool> {
        val response = service.updateTool(command = Pair(id, request))
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteTool(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteToolById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}