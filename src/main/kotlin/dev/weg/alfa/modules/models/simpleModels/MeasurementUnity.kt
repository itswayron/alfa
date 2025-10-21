package dev.weg.alfa.modules.models.simpleModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.simpleModels.Group
import jakarta.persistence.*


@Entity(name = "MeasurementUnit")
@Table(name ="measurement_units")
@JsonIgnoreProperties("hibernateLazyInitializer","handler")
data class MeasurementUnity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,
    @Column(unique = true, nullable = false, length = 10)
    val name: String,
)
{
    public constructor() : this(id = null, name = "")
}