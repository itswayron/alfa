package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.lending.LendingRequest
import dev.weg.alfa.modules.models.lending.LendingResponse
import dev.weg.alfa.modules.models.lending.ReturnLending
import dev.weg.alfa.modules.services.LendingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.LENDING)
class LendingController(private val service: LendingService) {

    @PostMapping
    fun createLending(@RequestBody request: LendingRequest): ResponseEntity<LendingResponse> {
        val response = service.createLending(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllLending(): ResponseEntity<List<LendingResponse>> {
        val response = service.getAllLending()
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteLending(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteLendingById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PatchMapping("/{id}/return")
    fun returnLending(
        @PathVariable id: Int,
        @RequestBody returnLending: ReturnLending,
    ): ResponseEntity<LendingResponse> {
        val response = service.returnTool(id, returnLending)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
