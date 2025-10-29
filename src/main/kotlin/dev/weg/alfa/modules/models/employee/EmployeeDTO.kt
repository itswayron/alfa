package dev.weg.alfa.modules.models.employee

import jakarta.validation.constraints.*

data class EmployeeDTO(
    @field:NotBlank(message = "name is mandatory")
    val name :  String,

    @field:NotNull(message = "the Id is mandatory")
    val sectorId : Int,
)