package dev.weg.alfa.modules.models.stock

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
        val groupName: String,
        val subgroupName: String,
        val measuramentUnitName: String,
        val imagePath: String? = null,
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
