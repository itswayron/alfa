package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "sector")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class Sector(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int = 0,
  @Column(name = "name")
  val name: String,
)
