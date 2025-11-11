package dev.weg.alfa.modules.models.stock

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Subgroup

data class StockPatch(
    val minimumAmount: Double? = null,
    val maximumAmount: Double? = null,
    val sectorId: Int? = null,
    val positionId: Int? = null
)

data class StockResponse(
    val id: Int,
    val itemInfo: ItemInfo,
    val amountInfo: AmountInfo,
    val sectorInfo: SectorInfo,
    val positionInfo: PositionInfo
) {
    data class ItemInfo(
        val itemId: Int,
        val itemCode: String,
        val itemDescription: String,
        val imagePath: String? = null,
        val dimensions: String? = null,
        val material: String? = null,
        val isActive: Boolean? = null,
        val supplier: BusinessPartner? = null,
        val group: Group,
        val subgroup: Subgroup,
        val measurementUnit: MeasurementUnity,
    )

    data class AmountInfo(
        val currentAmount: Double,
        val minimumAmount: Double?,
        val maximumAmount: Double?,
        val averagePrice: Double,
        val priceInMoney: Double,
    )

    data class SectorInfo(
        val sectorId: Int,
        val sectorName: String,
    )

    data class PositionInfo(
        val positionId: Int,
        val positionFloor: String,
        val positionSide: String,
        val positionColumn: String,
        val positionBox: String,
    )
}

data class StockRequest(
    val itemId: Int,
    val minimumAmount: Double? = null,
    val maximumAmount: Double? = null,
    val sectorId: Int,
    val positionId: Int,
)
