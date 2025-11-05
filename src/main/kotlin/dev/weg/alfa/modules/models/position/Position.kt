package dev.weg.alfa.modules.models.position

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "position")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Position(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val floor: String,
    val side: String,
    @Column(name = "\"column\"")
    val column: String,
    val box: String,
)
