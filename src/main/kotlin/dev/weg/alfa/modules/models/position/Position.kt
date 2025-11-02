package dev.weg.alfa.modules.models.position

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "position")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class Position(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int = 0 ,
  @Column(name = "floor")
  val floor: String,
  @Column(name = "side")
  val side: String,
  @Column(name = "column")
  val column: String,
  @Column(name = "box")
  val box: String="",
)
