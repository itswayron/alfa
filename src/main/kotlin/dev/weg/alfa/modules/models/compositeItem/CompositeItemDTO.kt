package dev.weg.alfa.modules.models.compositeItem

import dev.weg.alfa.modules.models.item.ItemRequest
import dev.weg.alfa.modules.models.position.PositionRequest

data class CompositeItemRequest(
    val item: ItemRequest,
    val position: PositionRequest,
    val stock: CompositeStockRequest
) {
    data class CompositeStockRequest(
        val minimumAmount: Double? = null,
        val maximumAmount: Double? = null,
        val sectorId: Int
    )
}
