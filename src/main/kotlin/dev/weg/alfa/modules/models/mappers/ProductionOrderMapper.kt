package dev.weg.alfa.modules.models.mappers

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.productionOrder.ProductionOrder
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderPatch
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderRequest
import dev.weg.alfa.modules.models.productionOrder.ProductionOrderResponse

fun ProductionOrderRequest.toEntity(partner: BusinessPartner): ProductionOrder = ProductionOrder(
    id = 0,
    code = this.code,
    document = this.document,
    date = this.date,
    businessPartner = partner
)

fun ProductionOrder.toResponse(): ProductionOrderResponse = ProductionOrderResponse(
    id = this.id,
    code = this.code,
    document = this.document,
    date = this.date,
    businessPartner = this.businessPartner.name
)

fun ProductionOrder.applyPatch(patch: ProductionOrderPatch, partner: BusinessPartner?): ProductionOrder = ProductionOrder(
    id = this.id,
    code = patch.code ?: this.code,
    document = patch.document ?: this.document,
    date = patch.date ?: this.date,
    businessPartner = partner ?: this.businessPartner
)
