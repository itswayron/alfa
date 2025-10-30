package dev.weg.alfa.modules.repositories.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MovementStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovementStatusRepository : JpaRepository<MovementStatus, Int>
