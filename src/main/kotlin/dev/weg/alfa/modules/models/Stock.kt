package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.simpleModels.Sector
import java.time.LocalDateTime

data class Stock(
  val id: Int,
  val item: Item,
  val currentAmount: Double,
  val minimumAmount: Double?,
  val maximumAmount: Double?,
  val currentValueInMoney: Double,
  val expiredDate: LocalDateTime,
  val averagePrice: Double,
  val sector: Sector,
  val position: Position,
)
