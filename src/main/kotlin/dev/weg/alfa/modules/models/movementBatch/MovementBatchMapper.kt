package dev.weg.alfa.modules.models.movementBatch

import dev.weg.alfa.infra.persistence.specification.MovementBatchSpecificationBuilder
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.movement.MovementResponse
import org.springframework.data.jpa.domain.Specification

fun MovementBatchRequest.toEntity(partner: BusinessPartner?): MovementBatch =
    MovementBatch(
        id = 0,
        code = this.code,
        document = this.document,
        date = this.date,
        businessPartner = partner
    )

fun MovementBatch.toResponse(movementsList: List<MovementResponse>): MovementBatchResponse =
    MovementBatchResponse(
        id = this.id,
        code = this.code,
        document = this.document,
        date = this.date,
        businessPartner = this.businessPartner?.name,
        observation = this.observation,
        movementList = movementsList,
    )


fun MovementBatch.applyPatch(patch: MovementBatchPatch, partner: BusinessPartner?): MovementBatch =
    MovementBatch(
        id = this.id,
        code = patch.code ?: this.code,
        document = patch.document ?: this.document,
        date = patch.date ?: this.date,
        observation = patch.observation ?: this.observation,
        businessPartner = partner ?: this.businessPartner,
    )

fun MovementBatchFilter.toSpecification() : Specification<MovementBatch> =
    MovementBatchSpecificationBuilder()
        .whereCode(this.code)
        .whereDocument(this.document)
        .whereBusinessPartner(this.partnerId)
        .whereDateFrom(this.startDate)
        .whereDateTo(this.endDate)
        .build()
