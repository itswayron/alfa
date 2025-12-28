package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.simpleModels.LendingStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiRoutes.LENDING_STATUS)
class LendingStatusController {

    @GetMapping
    fun getAllStatusLending(): ResponseEntity<List<LendingStatus>> {
        val status = LendingStatus.entries.toList()
        return ResponseEntity(status, HttpStatus.OK)
    }
}
