package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "subgroup")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class Subgroup( // (EPI, Escritório, ferramentas, insumo de solda etc)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(unique = true, nullable = false, length = 100)
    val name: String,
)