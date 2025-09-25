package dev.weg.alfa.modules.models

import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

data class Item(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int,
  val code: String,
  val description: String,
  val group: Group,
  val subgroup: Subgroup,
  val dimensions: String,
  val material: String,
  val isActive: Boolean,
  val imagePath: String,
  val measurementUnity: MeasurementUnity,
  val mainSupplier: BusinessPartner,
)
