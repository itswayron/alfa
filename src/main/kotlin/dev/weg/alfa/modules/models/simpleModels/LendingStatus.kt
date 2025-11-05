package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "statuslending")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class LendingStatus (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val lendingStatus: String,
    )
