package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import dev.weg.alfa.modules.services.simpleEntities.LendingStatusService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(ApiRoutes.LENDING_STATUS)
class LendingStatusController(private val service: LendingStatusService) {

    @GetMapping
    fun getAllStatusLending(): ResponseEntity<List<LendingStatus>> {
        val response = service.getAllStatusLending()
        return ResponseEntity(response, HttpStatus.OK)
    }
}
