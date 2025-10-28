package dev.weg.alfa.modules.repositories.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MovementType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovementTypeRepository : JpaRepository<MovementType, Int>


