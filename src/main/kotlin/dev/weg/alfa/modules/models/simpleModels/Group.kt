package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.*

@Entity
@Table(name = "groups")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class Group(  // (Consumos, indireto, direto)
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  val id: Int = 0,
  @Column(unique = true, nullable = false, length = 100)
  val name: String,
)
