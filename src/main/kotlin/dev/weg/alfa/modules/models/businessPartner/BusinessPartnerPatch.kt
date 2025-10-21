package dev.weg.alfa.modules.models.businessPartner

data class BusinessPartnerPatch(
    val name: String? = null,
    val cnpj: String? = null,
    val relation: String? = null,
)
