package dev.weg.alfa.modules.controllers.simpleEntities

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.NameRequest
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.services.simpleEntities.MeasurementUnityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.MEASUREMENT_UNITS)
    class MeasurementUnityController(private val service: MeasurementUnityService) {

    @PostMapping
    fun createMeasurementUnity(@RequestBody request: NameRequest): ResponseEntity<MeasurementUnity> {
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createMeasurementUnity(request))
        return response
    }

    @GetMapping
    fun getAllMeasurementUnity(): ResponseEntity<List<MeasurementUnity>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.getAllMeasurementUnity())
        return response
    }

    @PutMapping("/{id}")
    fun updateMeasurementUnity(@PathVariable id: Int, @RequestBody request: NameRequest): ResponseEntity<MeasurementUnity> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.editMeasurementUnity(Pair(id, request)))
        return response
    }

    @DeleteMapping("/{id}")
    fun deleteMeasurementUnity(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deleteMeasurementUnityById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
