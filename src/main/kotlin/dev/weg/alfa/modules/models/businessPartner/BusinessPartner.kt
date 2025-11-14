package dev.weg.alfa.modules.models.businessPartner

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "business_partner")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class BusinessPartner(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "\"name\"")
    val name: String,
    val cnpj: String,
    val relation: String,
)
