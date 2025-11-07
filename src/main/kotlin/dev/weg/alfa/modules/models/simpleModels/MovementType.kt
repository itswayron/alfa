package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "movement_type")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class MovementType( // (entradas, saídas, movimentação interna, consumo etc)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val name: String,
    val affectsAveragePrice: Boolean = true,
    val quantitySign: Int? = null,
)
