package dev.weg.alfa.infra.audit

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.infra.audit.model.Audit
import dev.weg.alfa.infra.audit.model.AuditFilter
import dev.weg.alfa.infra.audit.services.AuditService
import dev.weg.alfa.modules.models.dtos.PageDTO
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping(ApiRoutes.AUDIT)
class AuditController(private val service: AuditService) {

    @GetMapping
    fun getAllAudits(
        @RequestParam(required = false) actor: String?,
        @RequestParam(required = false) action: String?,
        @RequestParam(required = false) beforeContains: String?,
        @RequestParam(required = false) afterContains: String?,
        @RequestParam(required = false) timestampFrom: Instant?,
        @RequestParam(required = false) timestampTo: Instant?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "timestamp") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<Audit>> {

        val filter = AuditFilter(
            actor = actor,
            action = action,
            beforeContains = beforeContains,
            afterContains = afterContains,
            timestampFrom = timestampFrom,
            timestampTo = timestampTo,
        )

        val pageable =
            PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))

        val response = service.getAll(filter, pageable)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
