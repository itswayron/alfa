package dev.weg.alfa.modules.models.item

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import dev.weg.alfa.modules.models.simpleModels.Group
import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "item")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class Item(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val code: String,
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subgroup_id", nullable = false)
    val subgroup: Subgroup,

    val dimensions: String? = null,
    val material: String? = null,
    val isActive: Boolean = true,
    val imagePath: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_unity_id", nullable = false)
    val measurementUnity: MeasurementUnity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_supplier_id")
    val mainSupplier: BusinessPartner? = null,
)
