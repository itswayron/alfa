package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.simpleModels.StatusLending
import dev.weg.alfa.modules.services.simpleEntities.StatusLendingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(ApiRoutes.STATUS_LENDING)
class StatusLendingController(private val service: StatusLendingService) {

    @GetMapping
    fun getAllStatusLending(): ResponseEntity<List<StatusLending>> {
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.getAllStatusLending())
        return response
    }
}
