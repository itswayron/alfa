package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.businessPartner.BusinessPartnerPatch
import dev.weg.alfa.modules.services.BusinessPartnerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.PARTNER)
class BusinessPartnerController(private val service: BusinessPartnerService) {
    @PostMapping
    fun createBusinessPartner(@RequestBody request: BusinessPartner): ResponseEntity<BusinessPartner> {
        val response = ResponseEntity.status(HttpStatus.CREATED).body(service.createPartner(request))
        return response
    }

    @GetMapping
    fun getAllBusinessPartners(): ResponseEntity<List<BusinessPartner>> {
        val response = ResponseEntity.status(HttpStatus.OK).body(service.findAllPartners())
        return response
    }

    @PatchMapping("/{id}")
    fun updateBusinessPartner(@PathVariable id: Int, @RequestBody request: BusinessPartnerPatch): ResponseEntity<BusinessPartner> {
        val response = ResponseEntity.status(HttpStatus.OK).body(
            service.updatePartner(Pair(id, request)))
        return response
    }

    @DeleteMapping("/{id}")
    fun deleteBusinessPartner(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deletePartner(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
