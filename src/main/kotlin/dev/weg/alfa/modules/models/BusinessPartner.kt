package dev.weg.alfa.modules.models

data class BusinessPartner(
  val id: Int,
  val name: String,
  val cnpj: String,
  val relation: String, // cliente ou fornecedor
)
