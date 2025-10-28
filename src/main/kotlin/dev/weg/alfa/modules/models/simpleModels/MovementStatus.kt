package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "movement_status")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class MovementStatus(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int =0,
  @Column(name = "name")
  val name: String
)