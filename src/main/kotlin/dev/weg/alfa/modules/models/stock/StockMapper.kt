package dev.weg.alfa.modules.models.stock

import dev.weg.alfa.modules.models.item.Item
import dev.weg.alfa.modules.models.position.Position
import dev.weg.alfa.modules.models.simpleModels.Sector

fun StockRequest.toEntity(item: Item, sector: Sector, position: Position): Stock =
    Stock(
        id = 0,
        item = item,
        averagePrice = 0.0,
        currentAmount = 0.0,
        minimumAmount = this.minimumAmount,
        maximumAmount = this.maximumAmount,
        sector = sector,
        position = position
    )

fun Stock.toResponse(): StockResponse =
    StockResponse(
        id = this.id,
        itemInfo = StockResponse.ItemInfo(
            itemId = this.item.id,
            itemCode = this.item.code,
            itemDescription = this.item.description,
            groupName = this.item.group.name,
            subgroupName = this.item.subgroup.name,
            measuramentUnitName = this.item.measurementUnity.name,
            imagePath = this.item.imagePath,
        ),
        amountInfo = StockResponse.AmountInfo(
            currentAmount = this.currentAmount,
            minimumAmount = this.minimumAmount,
            maximumAmount = this.maximumAmount,
            averagePrice = this.averagePrice,
            priceInMoney = this.currentAmount * this.averagePrice,

            ),
        sectorInfo = StockResponse.SectorInfo(
            sectorId = this.sector.id,
            sectorName = this.sector.name,
        ),
        positionInfo = StockResponse.PositionInfo(
            positionId = this.position.id,
            positionFloor = this.position.floor,
            positionSide = this.position.side,
            positionColumn = this.position.column,
            positionBox = this.position.box
        ),
    )

fun Stock.applyPatch(
    patch: StockPatch,
    sector: Sector? = null,
    position: Position? = null,
): Stock =
    Stock(
        id = this.id,
        item = this.item,
        averagePrice = this.averagePrice,
        currentAmount = this.currentAmount,
        minimumAmount = patch.minimumAmount ?: this.minimumAmount,
        maximumAmount = patch.maximumAmount ?: this.maximumAmount,
        sector = sector ?: this.sector,
        position = position ?: this.position
    )
