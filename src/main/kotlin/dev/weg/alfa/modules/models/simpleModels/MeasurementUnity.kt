package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "measurement_unity")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class MeasurementUnity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(unique = true, nullable = false, length = 10)
    val name: String,
)
