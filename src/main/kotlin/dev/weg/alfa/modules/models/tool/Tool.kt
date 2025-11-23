package dev.weg.alfa.modules.models.tool

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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
@Table(name = "tool")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class Tool(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val name: String,
    val description: String,
    val maximumUsages: Int,
    var actualUsages: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subgroup_id", nullable = false)
    val subgroup: Subgroup,
    var isLoan: Boolean,
)
