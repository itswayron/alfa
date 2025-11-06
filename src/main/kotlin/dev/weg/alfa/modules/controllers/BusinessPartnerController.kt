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
        val response = service.createPartner(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllBusinessPartners(): ResponseEntity<List<BusinessPartner>> {
        val response = service.findAllPartners()
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getBusinessPartnerById(@PathVariable id: Int): ResponseEntity<BusinessPartner> {
        val response = service.findPartnerById(id)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updateBusinessPartner(
        @PathVariable id: Int,
        @RequestBody request: BusinessPartnerPatch
    ): ResponseEntity<BusinessPartner> {
        val response = service.updatePartner(Pair(id, request))
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteBusinessPartner(@PathVariable id: Int): ResponseEntity<Unit> {
        service.deletePartner(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
