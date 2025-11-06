package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "lending_status")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class LendingStatus(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val lendingStatus: String,
)
