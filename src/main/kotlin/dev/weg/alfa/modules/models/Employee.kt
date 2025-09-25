package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.simpleModels.Sector

data class Employee(
  val id: Int,
  val name: String,
  val sector: Sector,
)
